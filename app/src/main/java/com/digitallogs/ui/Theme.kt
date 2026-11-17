package com.digitallogs.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Brand palette ───────────────────────────────────────────────────────────
val DeepNavy        = Color(0xFF0A0E1A)
val NavyCard        = Color(0xFF111827)
val NavyCardLight   = Color(0xFF1C2539)

val CyanVibrant     = Color(0xFF00D4FF)
val CyanSoft        = Color(0xFF00B4D8)
val PurpleVibrant   = Color(0xFF8B5CF6)
val PurpleSoft      = Color(0xFF7C3AED)
val GreenVibrant    = Color(0xFF10B981)
val GreenSoft       = Color(0xFF059669)
val OrangeVibrant   = Color(0xFFF59E0B)
val OrangeSoft      = Color(0xFFD97706)
val PinkVibrant     = Color(0xFFEC4899)

val GradientBlue1   = Color(0xFF1E3A8A)
val GradientBlue2   = Color(0xFF1D4ED8)
val GradientPurple1 = Color(0xFF4C1D95)
val GradientPurple2 = Color(0xFF7C3AED)

val TextPrimary     = Color(0xFFF1F5F9)
val TextSecondary   = Color(0xFF94A3B8)
val TextMuted       = Color(0xFF64748B)

private val DarkColorScheme = darkColorScheme(
    primary = CyanVibrant,
    secondary = PurpleVibrant,
    tertiary = GreenVibrant,
    background = DeepNavy,
    surface = NavyCard,
    onPrimary = DeepNavy,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = NavyCardLight,
    onSurfaceVariant = TextSecondary
)

@Composable
fun DigitalLogsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

// Gradient presets for cards
val gradientCyan    = listOf(Color(0xFF0891B2), Color(0xFF06B6D4))
val gradientPurple  = listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6))
val gradientGreen   = listOf(Color(0xFF065F46), Color(0xFF10B981))
val gradientOrange  = listOf(Color(0xFFB45309), Color(0xFFF59E0B))
val gradientPink    = listOf(Color(0xFF9D174D), Color(0xFFEC4899))
val gradientBlue    = listOf(Color(0xFF1E40AF), Color(0xFF3B82F6))
