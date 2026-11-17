package com.digitallogs.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores daily aggregated usage per app (from UsageStatsManager).
 * Unique on (packageName, date) — one row per app per day.
 * On conflict, the row is replaced with fresh data from the system.
 */
@Entity(
    tableName = "app_usage",
    indices = [Index(value = ["packageName", "date"], unique = true)]
)
data class AppUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,       // milliseconds
    val date: String,         // yyyy-MM-dd
    val timestamp: Long = System.currentTimeMillis()
)
