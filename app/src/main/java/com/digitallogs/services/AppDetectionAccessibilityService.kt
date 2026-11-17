package com.digitallogs.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.repository.UsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AppDetectionAccessibilityService : AccessibilityService() {

    @Inject lateinit var repository: UsageRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastPackageName: String = ""
    private var sessionStartTime: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        sessionStartTime = System.currentTimeMillis()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName == lastPackageName) return
        if (packageName == "com.digitallogs") return

        val now = System.currentTimeMillis()

        // Save the previous session
        if (lastPackageName.isNotEmpty() && sessionStartTime > 0L) {
            val duration = now - sessionStartTime
            if (duration > 1000L) {
                val prevPackage = lastPackageName
                val startTime = sessionStartTime
                scope.launch {
                    val appName = repository.getAppName(prevPackage)
                    repository.insertSession(
                        SessionEntity(
                            packageName = prevPackage,
                            appName = appName,
                            startTime = startTime,
                            endTime = now,
                            duration = duration,
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date())
                        )
                    )
                }
            }
        }

        lastPackageName = packageName
        sessionStartTime = now
        UsageTrackingService.currentForegroundApp = packageName
        UsageTrackingService.currentAppStartTime = now
    }

    override fun onInterrupt() {}
}
