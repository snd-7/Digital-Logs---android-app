package com.digitallogs.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.ui.*
import com.digitallogs.ui.viewmodel.ExportUiState

@Composable
fun ExportScreen(
    uiState: ExportUiState,
    onExport: () -> Unit,
    onShare: (String) -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060B18), DeepNavy)))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Spacer(Modifier.height(56.dp))
        Text("DIGITAL LOGS", color = CyanVibrant, fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 20.dp))
        Text("Export Data", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp))
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Export info card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(NavyCardLight)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TableChart, contentDescription = null,
                            tint = GreenVibrant, modifier = Modifier.size(28.dp))
                        Text("CSV Export", color = TextPrimary, fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)
                    }
                    Text("Export your digital usage logs to a CSV file compatible with Microsoft Excel and Google Sheets.",
                        color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CsvColumnRow("date", "Session date")
                        CsvColumnRow("app_name", "Application name")
                        CsvColumnRow("package_name", "Package identifier")
                        CsvColumnRow("session_start", "Session start time")
                        CsvColumnRow("session_end", "Session end time")
                        CsvColumnRow("duration_seconds", "Duration in seconds")
                    }
                }
            }

            // Export button
            Button(
                onClick = { if (!uiState.isExporting) onExport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(gradientCyan),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isExporting) {
                        CircularProgressIndicator(
                            color = DeepNavy,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null,
                                tint = DeepNavy)
                            Text("Export to CSV", color = DeepNavy, fontWeight = FontWeight.Bold,
                                fontSize = 16.sp)
                        }
                    }
                }
            }

            // Success state
            AnimatedVisibility(visible = uiState.exportSuccess && uiState.exportPath != null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenVibrant.copy(alpha = 0.15f))
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = GreenVibrant)
                            Column {
                                Text("Exportação concluída! ${uiState.rowCount} registos exportados.", color = GreenVibrant,
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(uiState.exportPath ?: "", color = TextMuted,
                                    fontSize = 10.sp, maxLines = 2)
                            }
                        }
                    }

                    Button(
                        onClick = { uiState.exportPath?.let { onShare(it) } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(gradientPurple),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null,
                                    tint = Color.White)
                                Text("Share File", color = Color.White,
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            // Error state
            uiState.error?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PinkVibrant.copy(alpha = 0.15f))
                        .padding(14.dp)
                ) {
                    Text("Error: $err", color = PinkVibrant, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun CsvColumnRow(column: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(column, color = CyanVibrant, fontSize = 11.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(120.dp))
        Text(description, color = TextMuted, fontSize = 11.sp)
    }
}
