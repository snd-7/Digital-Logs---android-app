package com.digitallogs.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.digitallogs.ui.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun WeeklyBarChart(
    data: List<Pair<String, Long>>,
    modifier: Modifier = Modifier
) {
    SectionCard(title = "Weekly Screen Time", modifier = modifier) {
        if (data.isEmpty()) {
            EmptyChartPlaceholder()
            return@SectionCard
        }
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            factory = { ctx ->
                BarChart(ctx).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawGridBackground(false)
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    setDrawBarShadow(false)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        textColor = TextMuted.toArgb()
                        textSize = 10f
                        granularity = 1f
                        valueFormatter = IndexAxisValueFormatter(data.map { it.first.takeLast(5) })
                    }
                    axisLeft.apply {
                        textColor = TextMuted.toArgb()
                        textSize = 10f
                        setDrawGridLines(true)
                        gridColor = NavyCardLight.toArgb()
                        axisLineColor = AndroidColor.TRANSPARENT
                        valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val mins = (value / 60000).toLong()
                                return if (mins >= 60) "${mins / 60}h" else "${mins}m"
                            }
                        }
                    }
                    axisRight.isEnabled = false

                    val entries = data.mapIndexed { i, (_, v) -> BarEntry(i.toFloat(), v.toFloat()) }
                    val dataSet = BarDataSet(entries, "Screen Time").apply {
                        val colors = listOf(
                            CyanVibrant, PurpleVibrant, GreenVibrant,
                            OrangeVibrant, PinkVibrant, CyanSoft, PurpleSoft
                        ).map { it.toArgb() }
                        setColors(colors)
                        setDrawValues(false)
                    }
                    this.data = BarData(dataSet).apply { barWidth = 0.65f }
                    animateY(800)
                    invalidate()
                }
            },
            update = { chart ->
                val entries = data.mapIndexed { i, (_, v) -> BarEntry(i.toFloat(), v.toFloat()) }
                val dataSet = BarDataSet(entries, "Screen Time").apply {
                    val colors = listOf(
                        CyanVibrant, PurpleVibrant, GreenVibrant,
                        OrangeVibrant, PinkVibrant, CyanSoft, PurpleSoft
                    ).map { it.toArgb() }
                    setColors(colors)
                    setDrawValues(false)
                }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first.takeLast(5) })
                chart.data = BarData(dataSet).apply { barWidth = 0.65f }
                chart.animateY(600)
                chart.invalidate()
            }
        )
    }
}

@Composable
fun AppUsageBarChart(
    apps: List<Pair<String, Long>>,
    formatDuration: (Long) -> String,
    modifier: Modifier = Modifier
) {
    SectionCard(title = "Top App Usage", modifier = modifier) {
        if (apps.isEmpty()) {
            EmptyChartPlaceholder()
            return@SectionCard
        }
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            factory = { ctx ->
                BarChart(ctx).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawGridBackground(false)
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    setFitBars(true)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        textColor = TextMuted.toArgb()
                        textSize = 9f
                        granularity = 1f
                        labelRotationAngle = -30f
                        valueFormatter = IndexAxisValueFormatter(apps.map {
                            it.first.take(8)
                        })
                    }
                    axisLeft.apply {
                        textColor = TextMuted.toArgb()
                        textSize = 9f
                        gridColor = NavyCardLight.toArgb()
                        axisLineColor = AndroidColor.TRANSPARENT
                        valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                            override fun getFormattedValue(value: Float) =
                                formatDuration(value.toLong())
                        }
                    }
                    axisRight.isEnabled = false

                    val entries = apps.mapIndexed { i, (_, v) -> BarEntry(i.toFloat(), v.toFloat()) }
                    val dataSet = BarDataSet(entries, "App Usage").apply {
                        val colors = listOf(
                            CyanVibrant, PurpleVibrant, GreenVibrant,
                            OrangeVibrant, PinkVibrant
                        ).map { it.toArgb() }
                        setColors(colors)
                        setDrawValues(false)
                    }
                    this.data = BarData(dataSet).apply { barWidth = 0.6f }
                    animateY(900)
                    invalidate()
                }
            }
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCardLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
private fun EmptyChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1929)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("No data yet", color = TextMuted, fontSize = 13.sp)
    }
}
