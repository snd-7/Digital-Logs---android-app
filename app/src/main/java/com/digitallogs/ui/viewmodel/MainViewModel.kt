package com.digitallogs.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitallogs.analytics.UsageAnalyticsManager
import com.digitallogs.data.models.AppStats
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.models.UsageAnalytics
import com.digitallogs.data.repository.UsageRepository
import com.digitallogs.utils.PermissionHelper
import com.digitallogs.utils.SeedDataHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val analytics: UsageAnalytics = UsageAnalytics(),
    val systemTopApps: List<AppStats> = emptyList(),
    val recentSessions: List<SessionEntity> = emptyList(),
    val hasUsagePermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val isShowingDemoData: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UsageRepository,
    private val analyticsManager: UsageAnalyticsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        initData()
        observeSessions()
    }

    private fun initData() {
        viewModelScope.launch {
            val hasPermission = PermissionHelper.hasUsageStatsPermission(context)

            if (hasPermission) {
                // ── Real data path ──────────────────────────────────────────
                // 1. Sync latest real usage from system into our DB
                analyticsManager.syncRealUsageToDatabase()
                // 2. Load analytics from DB (now contains real data)
                loadDashboard(isDemo = false)
            } else {
                // ── Demo data path ──────────────────────────────────────────
                // Only seeds if DB is truly empty
                val hasReal = analyticsManager.hasRealUsageData()
                SeedDataHelper.seedIfNoRealData(repository, context, hasReal)
                loadDashboard(isDemo = true)
            }
        }
    }

    fun loadDashboard(isDemo: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val hasPermission = PermissionHelper.hasUsageStatsPermission(context)

                // Always sync real data first when permission is available
                if (hasPermission) {
                    analyticsManager.syncRealUsageToDatabase()
                }

                val analytics  = analyticsManager.computeTodayAnalytics()
                val systemApps = if (hasPermission) {
                    analyticsManager.computeSystemUsageForToday()
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(
                        isLoading        = false,
                        analytics        = analytics,
                        systemTopApps    = systemApps,
                        isShowingDemoData = isDemo && !hasPermission,
                        error            = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            repository.getRecentSessions().collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions) }
            }
        }
    }

    /**
     * Called from MainActivity.onResume() and when permissions change.
     * If permission was just granted, clears demo data and loads real data.
     */
    fun updatePermissionState(hasUsage: Boolean, hasAccessibility: Boolean) {
        val previousUsage = _uiState.value.hasUsagePermission
        _uiState.update {
            it.copy(
                hasUsagePermission        = hasUsage,
                hasAccessibilityPermission = hasAccessibility
            )
        }

        // Permission was just granted — clear demo and load real data
        if (hasUsage && !previousUsage) {
            viewModelScope.launch {
                if (_uiState.value.isShowingDemoData) {
                    SeedDataHelper.clearDemoData(context)
                }
                analyticsManager.syncRealUsageToDatabase()
                loadDashboard(isDemo = false)
            }
        }
    }

    fun refresh() {
        val hasPermission = PermissionHelper.hasUsageStatsPermission(context)
        loadDashboard(isDemo = !hasPermission)
    }

    fun formatDuration(millis: Long): String = analyticsManager.formatDuration(millis)
}
