package com.digitallogs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.ui.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionList(
    sessions: List<SessionEntity>,
    formatDuration: (Long) -> String,
    modifier: Modifier = Modifier
) {
    if (sessions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No sessions recorded yet", color = TextMuted, fontSize = 14.sp)
        }
        return
    }

    val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sessions.take(20).forEachIndexed { idx, session ->
            val gradient = when (idx % 5) {
                0 -> gradientCyan
                1 -> gradientPurple
                2 -> gradientGreen
                3 -> gradientOrange
                else -> gradientPink
            }
            SessionRow(
                session = session,
                gradient = gradient,
                formatDuration = formatDuration,
                timeFmt = timeFmt
            )
        }
    }
}

@Composable
private fun SessionRow(
    session: SessionEntity,
    gradient: List<Color>,
    formatDuration: (Long) -> String,
    timeFmt: SimpleDateFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCardLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.radialGradient(gradient.map { it.copy(alpha = 0.2f) })),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = null,
                tint = gradient.first(),
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.appName,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${timeFmt.format(Date(session.startTime))} → ${timeFmt.format(Date(session.endTime))}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(gradient.first().copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = formatDuration(session.duration),
                color = gradient.first(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
