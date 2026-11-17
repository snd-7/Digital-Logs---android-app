package com.digitallogs

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.digitallogs.services.ScreenUnlockReceiver
import com.digitallogs.services.UsageTrackingService
import com.digitallogs.ui.*
import com.digitallogs.ui.viewmodel.ExportViewModel
import com.digitallogs.ui.viewmodel.MainViewModel
import com.digitallogs.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val exportViewModel: ExportViewModel by viewModels()

    private var unlockReceiver: ScreenUnlockReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startTrackingService()
        registerUnlockReceiver()

        setContent {
            DigitalLogsTheme {
                val uiState by mainViewModel.uiState.collectAsState()
                val exportState by exportViewModel.state.collectAsState()

                // Check permissions on each composition
                LaunchedEffect(Unit) {
                    checkAndUpdatePermissions()
                }

                AppNavHost(
                    uiState = uiState,
                    exportUiState = exportState,
                    formatDuration = mainViewModel::formatDuration,
                    onRefresh = {
                        checkAndUpdatePermissions()
                        mainViewModel.refresh()
                    },
                    onExport = { exportViewModel.exportCsv(this@MainActivity) },
                    onShare = { path -> exportViewModel.shareFile(this@MainActivity, path) },
                    onResetExport = { exportViewModel.resetState() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdatePermissions()
        mainViewModel.refresh()
    }

    private fun checkAndUpdatePermissions() {
        val hasUsage = PermissionHelper.hasUsageStatsPermission(this)
        val hasAccessibility = PermissionHelper.hasAccessibilityPermission(this)
        mainViewModel.updatePermissionState(hasUsage, hasAccessibility)
    }

    private fun startTrackingService() {
        val intent = Intent(this, UsageTrackingService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun registerUnlockReceiver() {
        unlockReceiver = ScreenUnlockReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(unlockReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unlockReceiver?.let { unregisterReceiver(it) }
        } catch (_: Exception) {}
    }
}
