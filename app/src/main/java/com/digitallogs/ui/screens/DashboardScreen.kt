package com.digitallogs.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.ui.*
import com.digitallogs.ui.components.*
import com.digitallogs.ui.viewmodel.DashboardUiState

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    formatDuration: (Long) -> String,
    onRefresh: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF060B18), DeepNavy, Color(0xFF0D1525))
                )
            )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = CyanVibrant
            )
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            // ─── Header ──────────────────────────────────────────────────────
            DashboardHeader(onRefresh = onRefresh)

            // ─── Demo data banner ─────────────────────────────────────────
            if (uiState.isShowingDemoData) {
                DemoBanner()
            }

            // ─── Hero: Total Screen Time ──────────────────────────────────
            HeroScreenTime(
                totalMs = uiState.analytics.totalScreenTime,
                formatDuration = formatDuration
            )

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ─── Stat grid ───────────────────────────────────────────
                StatsGrid(uiState = uiState, formatDuration = formatDuration)

                // ─── Weekly chart ────────────────────────────────────────
                val weeklyData = uiState.analytics.weeklyUsageData
                WeeklyBarChart(data = weeklyData)

                // ─── Hourly heatmap ──────────────────────────────────────
                HourlyHeatmap(hourlyData = uiState.analytics.hourlyUsageData)

                // ─── Category donut ──────────────────────────────────────
                CategoryDonutChart(
                    categories = uiState.analytics.categoryData,
                    formatDuration = formatDuration
                )

                // ─── Top Apps chart ──────────────────────────────────────
                val topAppsChartData = (uiState.analytics.topApps.takeIf { it.isNotEmpty() }
                    ?: uiState.systemTopApps)
                    .take(5)
                    .map { it.appName to it.totalDuration }
                AppUsageBarChart(
                    apps = topAppsChartData,
                    formatDuration = formatDuration
                )

                // ─── Top Apps list ───────────────────────────────────────
                SectionCard(title = "Most Used Apps") {
                    val displayApps = (uiState.analytics.topApps.takeIf { it.isNotEmpty() }
                        ?: uiState.systemTopApps).take(8)
                    TopAppsList(
                        apps = displayApps,
                        formatDuration = formatDuration
                    )
                }

                // ─── Peak hour ───────────────────────────────────────────
                PeakHourCard(hour = uiState.analytics.peakUsageHour)


            }
        }
    }
}

@Composable
private fun DashboardHeader(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "DIGITAL LOGS",
                color = CyanVibrant,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )
            Text(
                text = "Today's Usage",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NavyCardLight)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CyanVibrant)
        }
    }
}

@Composable
private fun HeroScreenTime(totalMs: Long, formatDuration: (Long) -> String) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0C1A3E),
                        Color(0xFF0F2050),
                        Color(0xFF112060)
                    )
                )
            )
    ) {
        // Glow accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            CyanVibrant.copy(alpha = 0.3f + glowOffset * 0.4f),
                            PurpleVibrant.copy(alpha = 0.5f + glowOffset * 0.3f),
                            CyanVibrant.copy(alpha = 0.3f + glowOffset * 0.4f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null,
                    tint = CyanVibrant, modifier = Modifier.size(16.dp))
                Text("SCREEN TIME", color = CyanVibrant, fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = formatDuration(totalMs),
                color = TextPrimary,
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 56.sp
            )

            Spacer(Modifier.height(4.dp))

            val hours = totalMs / 3_600_000f
            val status = when {
                hours < 2 -> "✓ Healthy usage" to GreenVibrant
                hours < 5 -> "⚡ Moderate usage" to OrangeVibrant
                else -> "⚠ High usage" to PinkVibrant
            }
            Text(status.first, color = status.second, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatsGrid(uiState: DashboardUiState, formatDuration: (Long) -> String) {
    val analytics = uiState.analytics
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "UNLOCKS",
                value = "${analytics.unlockCount}",
                subtitle = "times today",
                icon = Icons.Default.LockOpen,
                gradient = gradientPurple,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "SESSIONS",
                value = "${analytics.sessionCount}",
                subtitle = "today",
                icon = Icons.Default.Timeline,
                gradient = gradientCyan,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "AVG SESSION",
                value = formatDuration(analytics.averageSessionDuration),
                subtitle = "per app",
                icon = Icons.Default.Timer,
                gradient = gradientGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "LONGEST",
                value = formatDuration(analytics.longestSession),
                subtitle = "single session",
                icon = Icons.Default.TrendingUp,
                gradient = gradientOrange,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                title = "APP SWITCHES",
                value = "${analytics.appSwitchFrequency.toInt()}",
                subtitle = "times today",
                icon = Icons.Default.SwapHoriz,
                gradient = gradientPink,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "PEAK HOUR",
                value = "${analytics.peakUsageHour}:00",
                subtitle = "most active",
                icon = Icons.Default.Schedule,
                gradient = gradientBlue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PeakHourCard(hour: Int) {
    val label = when {
        hour < 6 -> "Late Night"
        hour < 12 -> "Morning"
        hour < 18 -> "Afternoon"
        else -> "Evening"
    }
    val gradient = when {
        hour < 6 -> gradientPurple
        hour < 12 -> gradientOrange
        hour < 18 -> gradientCyan
        else -> gradientBlue
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(gradient.map { it.copy(alpha = 0.25f) })
            )
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("PEAK USAGE PERIOD", color = gradient.first().copy(alpha = 0.8f),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(label, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${hour}:00 – ${(hour + 1) % 24}:00", color = TextSecondary, fontSize = 13.sp)
            }
            Icon(Icons.Default.WbSunny, contentDescription = null,
                tint = gradient.first(), modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
private fun DemoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(OrangeVibrant.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = OrangeVibrant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "A mostrar dados de demonstração. Concede Usage Access para dados reais.",
                color = OrangeVibrant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )
        }
    }
}
