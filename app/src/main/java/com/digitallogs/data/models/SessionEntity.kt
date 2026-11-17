package com.digitallogs.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores individual app sessions detected by AccessibilityService.
 * Unique on (packageName, startTime) — prevents duplicate session inserts.
 */
@Entity(
    tableName = "sessions",
    indices = [Index(value = ["packageName", "startTime"], unique = true)]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)
