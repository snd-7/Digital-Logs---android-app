package com.digitallogs.data.models

data class AppStats(
    val packageName: String,
    val appName: String,
    val totalDuration: Long,
    val sessionCount: Int,
    val lastUsed: Long
)

data class DailyStats(
    val date: String,
    val totalScreenTime: Long,
    val unlockCount: Int,
    val sessionCount: Int,
    val appSwitchCount: Int
)

data class AppCategory(
    val name: String,
    val totalDuration: Long,
    val color: Long  // ARGB color as Long
)

data class UsageAnalytics(
    val totalScreenTime: Long = 0L,
    val averageSessionDuration: Long = 0L,
    val longestSession: Long = 0L,
    val shortestSession: Long = 0L,
    val unlockCount: Int = 0,
    val sessionCount: Int = 0,
    val appSwitchFrequency: Float = 0f,
    val peakUsageHour: Int = 0,
    val topApps: List<AppStats> = emptyList(),
    val dailyUsage: List<DailyStats> = emptyList(),
    val weeklyUsageData: List<Pair<String, Long>> = emptyList(),
    // New: 24-element list, index = hour (0-23), value = ms used in that hour
    val hourlyUsageData: List<Long> = List(24) { 0L },
    // New: app usage broken down by category
    val categoryData: List<AppCategory> = emptyList()
)
