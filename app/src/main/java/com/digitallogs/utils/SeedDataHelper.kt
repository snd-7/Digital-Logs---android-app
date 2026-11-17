package com.digitallogs.utils

import android.content.Context
import com.digitallogs.data.database.DigitalLogsDatabase
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.models.UnlockEventEntity
import com.digitallogs.data.repository.UsageRepository
import java.text.SimpleDateFormat
import java.util.*

object SeedDataHelper {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // These are the ONLY packages ever inserted as demo data.
    // Used both for seeding and for cleanup.
    val DEMO_PACKAGES = listOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.whatsapp",
        "com.twitter.android",
        "com.google.android.gm",
        "com.spotify.music",
        "com.netflix.mediaclient"
    )

    private val demoApps = listOf(
        Triple("Instagram",  DEMO_PACKAGES[0], listOf(18, 25, 12, 32, 20, 15, 28)),
        Triple("YouTube",    DEMO_PACKAGES[1], listOf(45, 60, 30, 90, 55, 40, 75)),
        Triple("WhatsApp",   DEMO_PACKAGES[2], listOf(22, 18, 35, 14, 27, 31, 19)),
        Triple("Twitter/X",  DEMO_PACKAGES[3], listOf(12,  8, 15, 20, 11,  9, 17)),
        Triple("Gmail",      DEMO_PACKAGES[4], listOf( 8, 10,  6, 12,  9,  7, 11)),
        Triple("Spotify",    DEMO_PACKAGES[5], listOf(30, 55, 20, 45, 35, 25, 50)),
        Triple("Netflix",    DEMO_PACKAGES[6], listOf(60, 90, 45,120, 75, 30, 80))
    )

    /**
     * Seeds demo data only when:
     * - Usage Access permission is NOT granted, AND
     * - The database is completely empty across all 7 days
     */
    suspend fun seedIfNoRealData(
        repository: UsageRepository,
        context: Context,
        hasRealData: Boolean
    ) {
        if (hasRealData) return

        // Check the last 7 days — don't seed if ANY real data exists
        val cal = Calendar.getInstance()
        for (daysAgo in 0..6) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val date = dateFormat.format(cal.time)
            if (repository.getTotalScreenTime(date) > 0L) return
            if (repository.getSessionCountForDate(date) > 0) return
        }

        insertDemoData(repository, context)
    }

    /**
     * Wipes ALL demo data rows from every table.
     * Called immediately when Usage Access permission is granted.
     */
    suspend fun clearDemoData(context: Context) {
        val db = DigitalLogsDatabase.getInstance(context)
        // Delete demo rows by package name from app_usage and sessions
        db.appUsageDao().deleteDemoData(DEMO_PACKAGES)
        db.sessionDao().deleteDemoData(DEMO_PACKAGES)
        // Clear all unlock events (they were fake)
        db.unlockEventDao().clearAll()
    }

    private suspend fun insertDemoData(repository: UsageRepository, context: Context) {
        val db     = DigitalLogsDatabase.getInstance(context)
        val random = Random()
        val cal    = Calendar.getInstance()

        for (daysAgo in 6 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayDate = dateFormat.format(cal.time)

            val dayStart = cal.clone() as Calendar
            dayStart.set(Calendar.HOUR_OF_DAY, 8)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MILLISECOND, 0)

            var cursor = dayStart.timeInMillis

            for ((appName, pkg, minutesList) in demoApps) {
                val totalMins      = minutesList[daysAgo]
                val numSessions    = 2 + random.nextInt(3)
                val minsPerSession = maxOf(1, totalMins / numSessions)

                for (s in 0 until numSessions) {
                    val gapMs      = (10 + random.nextInt(20)) * 60_000L
                    val startTime  = cursor + s * gapMs
                    val durationMs = (minsPerSession + random.nextInt(4)) * 60_000L
                    val endTime    = startTime + durationMs

                    repository.insertSession(
                        SessionEntity(
                            packageName = pkg, appName = appName,
                            startTime = startTime, endTime = endTime,
                            duration = durationMs, date = dayDate
                        )
                    )
                    repository.insertAppUsage(
                        AppUsageEntity(
                            packageName = pkg, appName = appName,
                            startTime = startTime, endTime = endTime,
                            duration = durationMs, date = dayDate
                        )
                    )
                }
                cursor += (totalMins + 15) * 60_000L
            }

            // Fake unlock events (8–14 per day)
            val unlocks = 8 + random.nextInt(7)
            repeat(unlocks) {
                db.unlockEventDao().insertUnlockEvent(UnlockEventEntity(date = dayDate))
            }
        }
    }
}
