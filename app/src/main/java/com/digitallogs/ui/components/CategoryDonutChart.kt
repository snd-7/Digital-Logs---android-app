package com.digitallogs.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.digitallogs.data.models.AppCategory
import com.digitallogs.ui.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun CategoryDonutChart(
    categories: List<AppCategory>,
    formatDuration: (Long) -> String,
    modifier: Modifier = Modifier
) {
    SectionCard(title = "Screen Time by Category", modifier = modifier) {
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1929)),
                contentAlignment = Alignment.Center
            ) {
                Text("No category data yet", color = TextMuted, fontSize = 13.sp)
            }
            return@SectionCard
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Donut chart ──────────────────────────────────────────────
            AndroidView(
                modifier = Modifier.size(160.dp),
                factory = { ctx ->
                    PieChart(ctx).apply {
                        description.isEnabled = false
                        legend.isEnabled = false
                        isDrawHoleEnabled = true
                        holeRadius = 58f
                        transparentCircleRadius = 62f
                        setHoleColor(AndroidColor.TRANSPARENT)
                        setTransparentCircleColor(AndroidColor.TRANSPARENT)
                        setDrawEntryLabels(false)
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                        isRotationEnabled = false
                        setTouchEnabled(false)

                        val entries = categories.map { cat ->
                            PieEntry(cat.totalDuration.toFloat(), cat.name)
                        }
                        val colors = categories.map { cat ->
                            // Convert Long ARGB to Android Int color
                            cat.color.toInt()
                        }
                        val dataSet = PieDataSet(entries, "Categories").apply {
                            setColors(colors)
                            sliceSpace = 2f
                            setDrawValues(false)
                        }
                        this.data = PieData(dataSet)

                        // Center text: total
                        setDrawCenterText(true)
                        centerText = "Screen\nTime"
                        setCenterTextColor(AndroidColor.WHITE)
                        setCenterTextSize(10f)

                        animateY(900)
                        invalidate()
                    }
                },
                update = { chart ->
                    val entries = categories.map { cat ->
                        PieEntry(cat.totalDuration.toFloat(), cat.name)
                    }
                    val colors = categories.map { cat -> cat.color.toInt() }
                    val dataSet = PieDataSet(entries, "Categories").apply {
                        setColors(colors)
                        sliceSpace = 2f
                        setDrawValues(false)
                    }
                    chart.data = PieData(dataSet)
                    chart.animateY(600)
                    chart.invalidate()
                }
            )

            // ── Legend ───────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                val totalDuration = categories.sumOf { it.totalDuration }.takeIf { it > 0 } ?: 1L
                categories.take(6).forEach { cat ->
                    val pct = (cat.totalDuration * 100f / totalDuration).toInt()
                    val catColor = Color(cat.color)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(catColor)
                        )
                        Text(
                            text = cat.name,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Text(
                            text = "$pct%",
                            color = catColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Mini progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct / 100f)
                                .fillMaxHeight()
                                .background(catColor.copy(alpha = 0.7f))
                        )
                    }
                }
            }
        }

        // Duration row below chart
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.take(4).forEach { cat ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(cat.color).copy(alpha = 0.12f))
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatDuration(cat.totalDuration),
                            color = Color(cat.color),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cat.name,
                            color = TextMuted,
                            fontSize = 8.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
