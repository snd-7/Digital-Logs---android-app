package com.digitallogs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.digitallogs.ui.screens.*
import com.digitallogs.ui.viewmodel.ExportUiState
import com.digitallogs.ui.viewmodel.DashboardUiState

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard   : Screen("dashboard",   "Dashboard",  Icons.Default.Dashboard)
    object TopApps     : Screen("top_apps",    "Top Apps",   Icons.Default.BarChart)
    object Export      : Screen("export",      "Export",     Icons.Default.FileDownload)
    object Permissions : Screen("permissions", "Permissions",Icons.Default.Security)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.TopApps,
    Screen.Export,
    Screen.Permissions
)

@Composable
fun AppNavHost(
    uiState: DashboardUiState,
    exportUiState: ExportUiState,
    formatDuration: (Long) -> String,
    onRefresh: () -> Unit,
    onExport: () -> Unit,
    onShare: (String) -> Unit,
    onResetExport: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { DigitalLogsBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    uiState = uiState,
                    formatDuration = formatDuration,
                    onRefresh = onRefresh
                )
            }
            composable(Screen.TopApps.route) {
                TopAppsScreen(
                    topApps = uiState.analytics.topApps,
                    systemApps = uiState.systemTopApps,
                    formatDuration = formatDuration
                )
            }

            composable(Screen.Export.route) {
                ExportScreen(
                    uiState = exportUiState,
                    onExport = onExport,
                    onShare = onShare,
                    onReset = onResetExport
                )
            }
            composable(Screen.Permissions.route) {
                PermissionsScreen(
                    hasUsagePermission = uiState.hasUsagePermission,
                    hasAccessibilityPermission = uiState.hasAccessibilityPermission
                )
            }
        }
    }
}

@Composable
fun DigitalLogsBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF111827), Color(0xFF1A2539))
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavItem(
                        screen = screen,
                        isSelected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(screen: Screen, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected)
                            Brush.radialGradient(gradientCyan.map { it.copy(alpha = 0.25f) })
                        else Brush.radialGradient(listOf(Color.Transparent, Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = screen.label,
                    tint = if (isSelected) CyanVibrant else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CyanVibrant)
                )
            }
        }
    }
}
