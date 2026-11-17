package com.digitallogs.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.ui.*

@Composable
fun HourlyHeatmap(
    hourlyData: List<Long>,  // 24 values, index = hour, value = ms
    modifier: Modifier = Modifier
) {
    val maxVal = hourlyData.maxOrNull()?.takeIf { it > 0 } ?: 1L

    // Colour scale: dark navy (0 usage) → cyan (max usage)
    val colourLow  = Color(0xFF0F1929)
    val colourMid  = Color(0xFF0E4D6E)
    val colourHigh = CyanVibrant

    fun intensityColor(ms: Long): Color {
        val t = ms.toFloat() / maxVal.toFloat()
        return when {
            t <= 0f   -> colourLow
            t <= 0.5f -> lerp(colourLow, colourMid, t * 2f)
            else      -> lerp(colourMid, colourHigh, (t - 0.5f) * 2f)
        }
    }

    SectionCard(title = "Hourly Usage Heatmap", modifier = modifier) {

        // Label row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("0h", "6h", "12h", "18h", "23h").forEach { label ->
                Text(label, color = TextMuted, fontSize = 9.sp)
            }
        }

        Spacer(Modifier.height(4.dp))

        // Heatmap cells — 24 cells in a single row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            hourlyData.forEachIndexed { hour, ms ->
                val targetColor = intensityColor(ms)
                val animColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(600, delayMillis = hour * 20),
                    label = "heat_$hour"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.6f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(animColor),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Show hour label every 3 hours
                    if (hour % 6 == 0) {
                        Text(
                            text = "$hour",
                            color = if (ms > maxVal / 3) Color(0xFF0A0E1A) else TextMuted,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Less", color = TextMuted, fontSize = 9.sp)
            // Gradient strip
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colourLow, colourMid, colourHigh)
                        )
                    )
            )
            Text("More", color = TextMuted, fontSize = 9.sp)

            // Peak indicator
            val peakHour = hourlyData.indices.maxByOrNull { hourlyData[it] } ?: 0
            if (hourlyData[peakHour] > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyanVibrant.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Peak: ${peakHour}:00",
                        color = CyanVibrant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
