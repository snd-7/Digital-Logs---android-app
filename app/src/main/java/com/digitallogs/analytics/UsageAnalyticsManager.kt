package com.digitallogs.analytics

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.digitallogs.data.models.AppCategory
import com.digitallogs.data.models.AppStats
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.UsageAnalytics
import com.digitallogs.data.repository.UsageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageAnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UsageRepository
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Returns true if UsageStatsManager has real data for today.
     * Used to decide whether to show demo data.
     */
    fun hasRealUsageData(): Boolean {
        return try {
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis
            val stats = repository.getSystemUsageStats(beginTime, endTime)
            stats.isNotEmpty()
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    suspend fun computeTodayAnalytics(): UsageAnalytics {
        val today          = dateFormat.format(Date())
        val totalScreenTime = repository.getTotalScreenTime(today)
        // Prefer system-level unlock count (more accurate than broadcast receiver)
        val unlockCount    = getSystemUnlockCount().takeIf { it > 0 }
                             ?: repository.getUnlockCountForDate(today)
        // Sessions from AccessibilityService DB
        var sessionCount   = repository.getSessionCountForDate(today)
        var avgSession     = repository.getAverageSessionDuration(today)
        var longest        = repository.getLongestSession(today)
        var shortest       = repository.getShortestSession(today)

        // Fallback: derive session metrics from UsageStatsManager if AccessibilityService
        // has not collected anything yet
        if (sessionCount == 0) {
            val systemSessions = getSystemSessionMetrics(today)
            sessionCount = systemSessions.first
            avgSession   = systemSessions.second
            longest      = systemSessions.third
        }

        val topApps        = repository.getTopAppsForDate(today)
        val weekly         = repository.getWeeklyUsage()
        val appSwitchFreq  = sessionCount.toFloat()
        val peakHour       = computePeakUsageHour()
        val hourly         = computeHourlyUsage()
        val allApps        = topApps.ifEmpty { computeSystemUsageForToday() }
        val categories     = computeCategoryBreakdown(allApps)

        return UsageAnalytics(
            totalScreenTime        = totalScreenTime,
            averageSessionDuration = avgSession,
            longestSession         = longest,
            shortestSession        = shortest,
            unlockCount            = unlockCount,
            sessionCount           = sessionCount,
            appSwitchFrequency     = appSwitchFreq,
            peakUsageHour          = peakHour,
            topApps                = topApps,
            weeklyUsageData        = weekly,
            hourlyUsageData        = hourly,
            categoryData           = categories
        )
    }

    /**
     * Reads top apps directly from UsageStatsManager (system-level, real data).
     * Returns empty list if permission not granted.
     */
    suspend fun computeSystemUsageForToday(): List<AppStats> {
        return try {
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis

            val stats = repository.getSystemUsageStats(beginTime, endTime)
            stats
                .sortedByDescending { it.totalTimeInForeground }
                .take(10)
                .map { stat ->
                    AppStats(
                        packageName  = stat.packageName,
                        appName      = repository.getAppName(stat.packageName),
                        totalDuration = stat.totalTimeInForeground,
                        sessionCount = 0,
                        lastUsed     = stat.lastTimeUsed
                    )
                }
        } catch (_: SecurityException) {
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Sync today's real usage from UsageStatsManager into the Room database.
     * This replaces any existing entries for the same package+date combination.
     */
    suspend fun syncRealUsageToDatabase() {
        try {
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis
            val today     = dateFormat.format(Date())

            val stats = repository.getSystemUsageStats(beginTime, endTime)
            if (stats.isEmpty()) return

            // Delete today's aggregated rows before re-inserting fresh data.
            // This prevents duration from accumulating on every sync call.
            repository.clearAppUsageForDate(today)

            for (stat in stats) {
                if (stat.totalTimeInForeground < 5_000L) continue
                val appName = repository.getAppName(stat.packageName)
                repository.insertAppUsage(
                    AppUsageEntity(
                        packageName = stat.packageName,
                        appName     = appName,
                        startTime   = stat.firstTimeStamp,
                        endTime     = stat.lastTimeUsed,
                        duration    = stat.totalTimeInForeground,
                        date        = today
                    )
                )
            }
        } catch (_: SecurityException) {
            // Permission not granted — skip
        } catch (_: Exception) {
            // Any other error — skip
        }
    }

    /**
     * Derives session count, avg duration and longest session
     * directly from UsageStatsManager when AccessibilityService data is unavailable.
     * Uses ACTIVITY_RESUMED / ACTIVITY_PAUSED events to reconstruct sessions.
     */
    private fun getSystemSessionMetrics(today: String): Triple<Int, Long, Long> {
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis

            // Use daily usage stats to count apps opened as a proxy for sessions
            val stats = repository.getSystemUsageStats(beginTime, endTime)
            if (stats.isEmpty()) return Triple(0, 0L, 0L)

            // Each app's usage = at least 1 session; estimate sessions from launch count
            // UsageStats doesn't give session count directly, so we approximate:
            // total foreground time / 5 minutes = rough session count
            var totalSessions = 0
            var totalDuration = 0L
            var longestMs = 0L

            for (stat in stats) {
                if (stat.totalTimeInForeground < 5_000L) continue
                val estimatedSessions = maxOf(1,
                    (stat.totalTimeInForeground / (5 * 60_000L)).toInt()
                )
                totalSessions += estimatedSessions
                totalDuration += stat.totalTimeInForeground
                if (stat.totalTimeInForeground > longestMs) {
                    longestMs = stat.totalTimeInForeground
                }
            }

            val avgMs = if (totalSessions > 0) totalDuration / totalSessions else 0L
            Triple(totalSessions, avgMs, longestMs)
        } catch (_: SecurityException) { Triple(0, 0L, 0L) }
          catch (_: Exception)         { Triple(0, 0L, 0L) }
    }

    private fun getSystemUnlockCount(): Int {
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis

            val events = usm.queryEvents(beginTime, endTime)
            var count = 0
            val event = UsageEvents.Event()
            while (events?.hasNextEvent() == true) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) count++
            }
            count
        } catch (_: SecurityException) { 0 }
          catch (_: Exception) { 0 }
    }

    private fun computePeakUsageHour(): Int {
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            val beginTime = cal.timeInMillis

            val events      = usm.queryEvents(beginTime, endTime)
            val hourlyUsage = IntArray(24)
            val event       = android.app.usage.UsageEvents.Event()
            while (events?.hasNextEvent() == true) {
                events.getNextEvent(event)
                val hour = Calendar.getInstance()
                    .apply { timeInMillis = event.timeStamp }
                    .get(Calendar.HOUR_OF_DAY)
                hourlyUsage[hour]++
            }
            hourlyUsage.indices.maxByOrNull { hourlyUsage[it] } ?: 20
        } catch (_: SecurityException) {
            20
        } catch (_: Exception) {
            20
        }
    }


    /**
     * Returns a 24-element list where index = hour of day (0-23)
     * and value = total milliseconds of screen use in that hour today.
     */
    fun computeHourlyUsage(): List<Long> {
        val hourly = LongArray(24)
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val beginTime = cal.timeInMillis

            val events = usm.queryEvents(beginTime, endTime) ?: return hourly.toList()
            val event = UsageEvents.Event()
            var lastResumeHour = -1
            var lastResumeTime = 0L

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        lastResumeHour = Calendar.getInstance()
                            .apply { timeInMillis = event.timeStamp }
                            .get(Calendar.HOUR_OF_DAY)
                        lastResumeTime = event.timeStamp
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (lastResumeTime > 0) {
                            val duration = event.timeStamp - lastResumeTime
                            // Distribute duration across hours it spans
                            var remaining = duration
                            var cursor = lastResumeTime
                            while (remaining > 0) {
                                val curCal = Calendar.getInstance().apply { timeInMillis = cursor }
                                val hour = curCal.get(Calendar.HOUR_OF_DAY)
                                // ms until end of this hour
                                val endOfHour = Calendar.getInstance().apply {
                                    timeInMillis = cursor
                                    set(Calendar.MINUTE, 59)
                                    set(Calendar.SECOND, 59)
                                    set(Calendar.MILLISECOND, 999)
                                }.timeInMillis
                                val sliceMs = minOf(remaining, endOfHour - cursor + 1)
                                if (hour in 0..23) hourly[hour] += sliceMs
                                remaining -= sliceMs
                                cursor += sliceMs
                            }
                            lastResumeTime = 0L
                        }
                    }
                }
            }
            hourly.toList()
        } catch (_: SecurityException) { hourly.toList() }
          catch (_: Exception) { hourly.toList() }
    }

    /**
     * Classifies apps into categories and returns time per category.
     */
    fun computeCategoryBreakdown(apps: List<AppStats>): List<AppCategory> {
        // Category rules based on package name keywords
        val categoryMap = mapOf(
            "Social"        to listOf("instagram","facebook","twitter","tiktok","snapchat","whatsapp","telegram","messenger","discord","linkedin","pinterest","reddit","tumblr","wechat","viber","signal"),
            "Entertainment" to listOf("youtube","netflix","spotify","tidal","deezer","twitch","disney","hbo","prime","soundcloud","pandora","plex","kodi","vlc","tvseries","crunchyroll"),
            "Games"         to listOf("game","clash","candy","pubg","fortnite","minecraft","roblox","pokemon","among","brawl","hearthstone","chess","sudoku","puzzle","casino","royale"),
            "Productivity"  to listOf("gmail","outlook","drive","docs","sheets","office","word","excel","notion","slack","teams","zoom","meet","calendar","clock","notes","todo","trello","asana"),
            "Navigation"    to listOf("maps","waze","uber","bolt","lyft","grab","citymapper","moovit","transit","navigation","gps"),
            "Browser"       to listOf("chrome","firefox","safari","opera","brave","edge","browser","duck"),
            "Shopping"      to listOf("amazon","ebay","olx","aliexpress","wish","etsy","shopify","mercado","zara","shein"),
            "Health"        to listOf("health","fitness","strava","nike","myfitnesspal","sleep","calm","headspace","meditation","workout","gym"),
            "News"          to listOf("news","cnn","bbc","guardian","reddit","feedly","flipboard","medium")
        )

        val durationByCategory = mutableMapOf<String, Long>()
        val categoryColors = mapOf(
            "Social"        to 0xFF8B5CF6L,
            "Entertainment" to 0xFFEC4899L,
            "Games"         to 0xFFF59E0BL,
            "Productivity"  to 0xFF10B981L,
            "Navigation"    to 0xFF00D4FFL,
            "Browser"       to 0xFF3B82F6L,
            "Shopping"      to 0xFFEF4444L,
            "Health"        to 0xFF22D3EEL,
            "News"          to 0xFF94A3B8L,
            "Other"         to 0xFF475569L
        )

        for (app in apps) {
            val pkg = app.packageName.lowercase()
            var matched = false
            for ((category, keywords) in categoryMap) {
                if (keywords.any { pkg.contains(it) }) {
                    durationByCategory[category] = (durationByCategory[category] ?: 0L) + app.totalDuration
                    matched = true
                    break
                }
            }
            if (!matched) {
                durationByCategory["Other"] = (durationByCategory["Other"] ?: 0L) + app.totalDuration
            }
        }

        return durationByCategory
            .filter { it.value > 0 }
            .map { (name, duration) ->
                AppCategory(name, duration, categoryColors[name] ?: 0xFF475569L)
            }
            .sortedByDescending { it.totalDuration }
    }

    fun formatDuration(millis: Long): String {
        val totalMinutes = millis / 60_000
        val hours        = totalMinutes / 60
        val minutes      = totalMinutes % 60
        return when {
            hours > 0   -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else        -> "${millis / 1000}s"
        }
    }
}
