package com.digitallogs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.data.models.AppStats
import com.digitallogs.ui.*
import com.digitallogs.ui.components.SectionCard
import com.digitallogs.ui.components.TopAppsList

@Composable
fun TopAppsScreen(
    topApps: List<AppStats>,
    systemApps: List<AppStats>,
    formatDuration: (Long) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060B18), DeepNavy)))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Spacer(Modifier.height(56.dp))
        Text(
            "DIGITAL LOGS",
            color = CyanVibrant,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            "Top Apps",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionCard(title = "Most Used Apps") {
                if (systemApps.isEmpty()) {
                    Text(
                        "Grant Usage Access permission to see app usage stats.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                } else {
                    TopAppsList(apps = systemApps, formatDuration = formatDuration)
                }
            }
        }
    }
}
