package com.digitallogs.data.repository

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.digitallogs.data.database.*
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.models.UnlockEventEntity
import com.digitallogs.data.models.AppStats
import com.digitallogs.data.models.DailyStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUsageDao: AppUsageDao,
    private val sessionDao: SessionDao,
    private val unlockEventDao: UnlockEventDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val pm: PackageManager = context.packageManager

    // ─── Insert operations ───────────────────────────────────────────────────

    suspend fun insertAppUsage(entity: AppUsageEntity) = appUsageDao.insertAppUsage(entity)

    suspend fun insertSession(entity: SessionEntity) = sessionDao.insertSession(entity)

    suspend fun insertUnlockEvent() {
        val today = dateFormat.format(Date())
        unlockEventDao.insertUnlockEvent(UnlockEventEntity(date = today))
    }

    // ─── Query operations ───────────────────────────────────────────────────

    fun getAppUsageForDate(date: String): Flow<List<AppUsageEntity>> =
        appUsageDao.getAppUsageForDate(date)

    fun getRecentSessions(): Flow<List<SessionEntity>> = sessionDao.getRecentSessions()

    fun getRecentAppUsage(): Flow<List<AppUsageEntity>> = appUsageDao.getRecentAppUsage()

    suspend fun getUnlockCountForDate(date: String): Int =
        unlockEventDao.getUnlockCountForDate(date)

    suspend fun getSessionCountForDate(date: String): Int =
        sessionDao.getSessionCountForDate(date)

    suspend fun getAverageSessionDuration(date: String): Long =
        sessionDao.getAverageSessionDuration(date) ?: 0L

    suspend fun getLongestSession(date: String): Long =
        sessionDao.getLongestSession(date) ?: 0L

    suspend fun getShortestSession(date: String): Long =
        sessionDao.getShortestSession(date) ?: 0L

    suspend fun getTotalScreenTime(date: String): Long =
        appUsageDao.getTotalScreenTimeForDate(date) ?: 0L

    suspend fun getTopAppsForDate(date: String): List<AppStats> {
        return appUsageDao.getTopAppsForDate(date).map { summary ->
            AppStats(
                packageName = summary.packageName,
                appName = summary.appName,
                totalDuration = summary.totalDuration,
                sessionCount = summary.sessionCount,
                lastUsed = summary.lastUsed
            )
        }
    }

    suspend fun getDailyStats(date: String): DailyStats {
        val totalTime = getTotalScreenTime(date)
        val unlocks = getUnlockCountForDate(date)
        val sessions = getSessionCountForDate(date)
        val apps = getTopAppsForDate(date)
        return DailyStats(
            date = date,
            totalScreenTime = totalTime,
            unlockCount = unlocks,
            sessionCount = sessions,
            appSwitchCount = apps.size
        )
    }

    suspend fun getWeeklyUsage(): List<Pair<String, Long>> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(cal.time)
        return appUsageDao.getDailyUsageForRange(startDate).map { it.date to it.totalDuration }
    }

    // ─── System usage stats ─────────────────────────────────────────────────

    suspend fun clearAppUsageForDate(date: String) = appUsageDao.clearForDate(date)

    fun getSystemUsageStats(beginTime: Long, endTime: Long): List<UsageStats> {
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime)
                ?.filter { it.totalTimeInForeground > 0 } ?: emptyList()
        } catch (_: SecurityException) {
            emptyList() // Usage Access permission not granted
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getAppName(packageName: String): String {
        return try {
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }

    // ─── CSV export ─────────────────────────────────────────────────────────

    suspend fun getAllSessionsAsCsv(): String {
        val sb = StringBuilder()
        sb.appendLine("date,app_name,package_name,session_start,session_end,duration_seconds")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sessions = sessionDao.getRecentSessions().first()
        sessions.forEach { s ->
            val date = dateFmt.format(Date(s.startTime))
            val start = sdf.format(Date(s.startTime))
            val end = sdf.format(Date(s.endTime))
            val durationSec = s.duration / 1000
            sb.appendLine("$date,\"${s.appName}\",${s.packageName},$start,$end,$durationSec")
        }
        return sb.toString()
    }
}
