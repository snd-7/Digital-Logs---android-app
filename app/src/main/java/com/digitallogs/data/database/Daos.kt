package com.digitallogs.data.database

import androidx.room.*
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.models.UnlockEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsageEntity): Long

    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY startTime DESC")
    fun getAppUsageForDate(date: String): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage ORDER BY startTime DESC LIMIT 100")
    fun getRecentAppUsage(): Flow<List<AppUsageEntity>>

    @Query("""
        SELECT packageName, appName, SUM(duration) as totalDuration, COUNT(*) as sessionCount, MAX(endTime) as lastUsed
        FROM app_usage 
        WHERE date = :date
        GROUP BY packageName
        ORDER BY totalDuration DESC
    """)
    suspend fun getTopAppsForDate(date: String): List<AppUsageSummary>

    @Query("""
        SELECT packageName, appName, SUM(duration) as totalDuration, COUNT(*) as sessionCount, MAX(endTime) as lastUsed
        FROM app_usage 
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY packageName
        ORDER BY totalDuration DESC
        LIMIT 10
    """)
    suspend fun getTopAppsForRange(startDate: String, endDate: String): List<AppUsageSummary>

    @Query("SELECT SUM(duration) FROM app_usage WHERE date = :date")
    suspend fun getTotalScreenTimeForDate(date: String): Long?

    @Query("SELECT date, SUM(duration) as totalDuration FROM app_usage WHERE date >= :startDate GROUP BY date ORDER BY date ASC")
    suspend fun getDailyUsageForRange(startDate: String): List<DailyUsage>

    @Query("DELETE FROM app_usage WHERE timestamp < :cutoff")
    suspend fun deleteOldRecords(cutoff: Long)

    @Query("DELETE FROM app_usage WHERE packageName IN (:packages)")
    suspend fun deleteDemoData(packages: List<String>)

    @Query("DELETE FROM app_usage")
    suspend fun clearAll()

    @Query("DELETE FROM app_usage WHERE date = :date")
    suspend fun clearForDate(date: String)
}

data class AppUsageSummary(
    val packageName: String,
    val appName: String,
    val totalDuration: Long,
    val sessionCount: Int,
    val lastUsed: Long
)

data class DailyUsage(
    val date: String,
    val totalDuration: Long
)

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions WHERE date = :date ORDER BY startTime DESC")
    fun getSessionsForDate(date: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY startTime DESC LIMIT 50")
    fun getRecentSessions(): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM sessions WHERE date = :date")
    suspend fun getSessionCountForDate(date: String): Int

    @Query("SELECT AVG(duration) FROM sessions WHERE date = :date")
    suspend fun getAverageSessionDuration(date: String): Long?

    @Query("SELECT MAX(duration) FROM sessions WHERE date = :date")
    suspend fun getLongestSession(date: String): Long?

    @Query("SELECT MIN(duration) FROM sessions WHERE date = :date AND duration > 0")
    suspend fun getShortestSession(date: String): Long?

    @Query("SELECT COUNT(*) FROM sessions WHERE date >= :startDate")
    suspend fun getTotalSessionsForRange(startDate: String): Int

    @Query("DELETE FROM sessions WHERE packageName IN (:packages)")
    suspend fun deleteDemoData(packages: List<String>)

    @Query("DELETE FROM sessions")
    suspend fun clearAll()
}

@Dao
interface UnlockEventDao {
    @Insert
    suspend fun insertUnlockEvent(event: UnlockEventEntity): Long

    @Query("SELECT COUNT(*) FROM unlock_events WHERE date = :date")
    suspend fun getUnlockCountForDate(date: String): Int

    @Query("SELECT COUNT(*) FROM unlock_events WHERE date >= :startDate")
    suspend fun getUnlockCountForRange(startDate: String): Int

    @Query("SELECT * FROM unlock_events WHERE date = :date ORDER BY timestamp DESC")
    fun getUnlockEventsForDate(date: String): Flow<List<UnlockEventEntity>>

    @Query("DELETE FROM unlock_events")
    suspend fun clearAll()
}
