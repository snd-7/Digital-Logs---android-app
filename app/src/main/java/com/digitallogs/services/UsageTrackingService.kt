package com.digitallogs.services

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.digitallogs.MainActivity
import com.digitallogs.R
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.repository.UsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UsageTrackingService : Service() {

    @Inject lateinit var repository: UsageRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        const val CHANNEL_ID = "usage_tracking_channel"
        const val NOTIFICATION_ID = 1001

        var currentForegroundApp: String = ""
        var currentAppStartTime: Long = 0L
        var isTracking: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        isTracking = true
        startPeriodicSync()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isTracking = false
        serviceScope.cancel()
    }

    private fun startPeriodicSync() {
        serviceScope.launch {
            // Initial delay to let the app settle
            delay(10_000L)
            while (isActive) {
                syncSystemUsageStats()
                delay(5 * 60 * 1000L) // every 5 minutes
            }
        }
    }

    private suspend fun syncSystemUsageStats() {
        // Wrap in try-catch: SecurityException thrown when Usage Access not granted
        try {
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val beginTime = cal.timeInMillis
            val today = dateFormat.format(Date())

            val stats = repository.getSystemUsageStats(beginTime, endTime)
            for (stat in stats) {
                if (stat.totalTimeInForeground > 5000L) {
                    val appName = repository.getAppName(stat.packageName)
                    val entity = AppUsageEntity(
                        packageName = stat.packageName,
                        appName = appName,
                        startTime = stat.firstTimeStamp,
                        endTime = stat.lastTimeUsed,
                        duration = stat.totalTimeInForeground,
                        date = today
                    )
                    repository.insertAppUsage(entity)
                }
            }
        } catch (e: SecurityException) {
            // Usage Access permission not granted — skip silently
        } catch (e: Exception) {
            // Any other error — skip silently, will retry in 5 min
        }
    }

    fun recordAppSession(
        packageName: String,
        appName: String,
        startTime: Long,
        endTime: Long
    ) {
        val duration = endTime - startTime
        if (duration < 1000L) return
        val today = dateFormat.format(Date())
        serviceScope.launch {
            try {
                repository.insertSession(
                    SessionEntity(
                        packageName = packageName,
                        appName = appName,
                        startTime = startTime,
                        endTime = endTime,
                        duration = duration,
                        date = today
                    )
                )
                repository.insertAppUsage(
                    AppUsageEntity(
                        packageName = packageName,
                        appName = appName,
                        startTime = startTime,
                        endTime = endTime,
                        duration = duration,
                        date = today
                    )
                )
            } catch (_: Exception) {}
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pi)
            .setSilent(true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_description)
        }
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}
