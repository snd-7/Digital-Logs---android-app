package com.digitallogs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
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
import com.digitallogs.data.models.AppStats
import com.digitallogs.ui.*

private val rankGradients = listOf(
    listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
    listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1)),
    listOf(Color(0xFFCD7C2F), Color(0xFFD97706)),
    gradientCyan,
    gradientPurple,
    gradientGreen,
    gradientPink,
    gradientBlue,
    gradientOrange,
    gradientPurple
)

@Composable
fun TopAppsList(
    apps: List<AppStats>,
    formatDuration: (Long) -> String,
    modifier: Modifier = Modifier
) {
    if (apps.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No app usage data yet", color = TextMuted, fontSize = 14.sp)
        }
        return
    }

    val maxDuration = apps.maxOfOrNull { it.totalDuration } ?: 1L

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        apps.forEachIndexed { idx, app ->
            AppUsageRow(
                rank = idx + 1,
                app = app,
                maxDuration = maxDuration,
                formatDuration = formatDuration,
                gradient = rankGradients.getOrElse(idx) { gradientCyan }
            )
        }
    }
}

@Composable
private fun AppUsageRow(
    rank: Int,
    app: AppStats,
    maxDuration: Long,
    formatDuration: (Long) -> String,
    gradient: List<Color>
) {
    val progress = if (maxDuration > 0) app.totalDuration.toFloat() / maxDuration else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "bar_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyCardLight)
    ) {
        // Progress fill bar behind
        Box(
            modifier = Modifier
                .fillMaxWidth(animProgress)
                .height(60.dp)
                .background(
                    Brush.horizontalGradient(gradient.map { it.copy(alpha = 0.12f) })
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(gradient.map { it.copy(alpha = 0.3f) })),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    color = gradient.first(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // App icon placeholder
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = gradient.first().copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = "${app.sessionCount} sessions",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Text(
                text = formatDuration(app.totalDuration),
                color = gradient.first(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
