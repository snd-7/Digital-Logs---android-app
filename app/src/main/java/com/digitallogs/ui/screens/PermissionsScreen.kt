package com.digitallogs.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.ui.*

@Composable
fun PermissionsScreen(
    hasUsagePermission: Boolean,
    hasAccessibilityPermission: Boolean
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
        Text("Permissions", color = TextPrimary, fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp))
        Text("Grant permissions to enable tracking", color = TextMuted, fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PermissionCard(
                title = "Usage Access",
                description = "Required to read app usage statistics from Android. This allows DigitalLogs to report which apps you've used and for how long.",
                icon = Icons.Default.QueryStats,
                gradient = gradientCyan,
                isGranted = hasUsagePermission,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )

            PermissionCard(
                title = "Accessibility Service",
                description = "Enables real-time foreground app detection — necessary for accurate session start/end times and app-switching frequency.",
                icon = Icons.Default.Accessibility,
                gradient = gradientPurple,
                isGranted = hasAccessibilityPermission,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )

            // Info card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1929))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null,
                            tint = GreenVibrant, modifier = Modifier.size(18.dp))
                        Text("Privacy Notice", color = GreenVibrant,
                            fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        "All data is stored locally on your device. DigitalLogs never uploads your usage data to any server. You own your data.",
                        color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: List<Color>,
    isGranted: Boolean,
    onGrant: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCardLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    if (isGranted) Brush.horizontalGradient(gradientGreen)
                    else Brush.horizontalGradient(gradient)
                )
        )
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    if (isGranted) gradientGreen.map { it.copy(alpha = 0.2f) }
                                    else gradient.map { it.copy(alpha = 0.2f) }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null,
                            tint = if (isGranted) GreenVibrant else gradient.first(),
                            modifier = Modifier.size(22.dp))
                    }
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                if (isGranted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Granted",
                        tint = GreenVibrant, modifier = Modifier.size(22.dp))
                }
            }

            Text(description, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)

            if (!isGranted) {
                Button(
                    onClick = onGrant,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(Brush.horizontalGradient(gradient),
                                shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Grant Permission", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenVibrant.copy(alpha = 0.12f))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Permission Granted ✓", color = GreenVibrant,
                        fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}
