# DigitalLogs 📱

A research-grade Android application for collecting, analyzing, and visualizing smartphone digital usage patterns.

---

## Features

- **Real-time app detection** via AccessibilityService
- **System usage statistics** via UsageStatsManager
- **Screen unlock counting** via broadcast receivers
- **Dashboard** with daily screen time hero card, stat grid, and charts
- **Top Apps** ranked list (tracked + system-level)
- **Session log** with start/end times and durations
- **CSV export** compatible with Excel and Google Sheets
- **Permissions guide** screen with one-tap navigation to system settings
- **Foreground service** for continuous background tracking
- **Dark, neon dashboard** design inspired by iOS Screen Time

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository |
| DI | Hilt |
| Database | Room |
| Charts | MPAndroidChart |
| Async | Kotlin Coroutines + Flow |
| Min SDK | API 26 (Android 8) |

---

## Project Structure

```
com.digitallogs/
├── DigitalLogsApp.kt          # Hilt Application class
├── MainActivity.kt            # Entry point, permission checks, service start
├── analytics/
│   └── UsageAnalyticsManager.kt   # Computes all metrics
├── data/
│   ├── models/
│   │   ├── AppUsageEntity.kt
│   │   ├── SessionEntity.kt
│   │   ├── UnlockEventEntity.kt
│   │   └── AppStats.kt            # Analytics data classes
│   ├── database/
│   │   ├── DigitalLogsDatabase.kt
│   │   └── Daos.kt                # AppUsageDao, SessionDao, UnlockEventDao
│   └── repository/
│       └── UsageRepository.kt
├── di/
│   └── AppModule.kt               # Hilt module
├── services/
│   ├── UsageTrackingService.kt    # Foreground service
│   ├── AppDetectionAccessibilityService.kt
│   ├── ScreenUnlockReceiver.kt
│   └── BootReceiver.kt
├── ui/
│   ├── Theme.kt                   # Colors, gradients, MaterialTheme
│   ├── Navigation.kt              # NavHost + bottom bar
│   ├── components/
│   │   ├── StatCard.kt
│   │   ├── TopAppsList.kt
│   │   ├── SessionList.kt
│   │   └── UsageChart.kt          # MPAndroidChart wrappers
│   ├── screens/
│   │   ├── DashboardScreen.kt
│   │   ├── TopAppsScreen.kt
│   │   ├── UsageLogScreen.kt
│   │   ├── ExportScreen.kt
│   │   └── PermissionsScreen.kt
│   └── viewmodel/
│       ├── MainViewModel.kt
│       └── ExportViewModel.kt
└── utils/
    └── PermissionHelper.kt
```

---

## Getting Started

### 1. Open in Android Studio

1. Clone or extract the project
2. Open **Android Studio** (Hedgehog or newer)
3. Open the `DigitalLogs/` root folder
4. Wait for Gradle sync to complete

### 2. Update `local.properties`

Set your Android SDK path:
```
sdk.dir=/Users/yourname/Library/Android/sdk
```

### 3. Build & Run

- Connect a real Android device (API 26+)
- Click **Run ▶**

> **Note:** Most usage tracking APIs require a real device. The emulator does not generate meaningful UsageStatsManager data.

---

## Required Permissions

### Usage Access (Special Permission)
- Go to **Settings → Apps → Special App Access → Usage Access**
- Enable **DigitalLogs**

Or tap **Grant Permission** on the Permissions screen inside the app.

### Accessibility Service
- Go to **Settings → Accessibility → Installed Services → DigitalLogs**
- Enable the service

This enables real-time session tracking (start/end times, app switches).

---

## Data Collected

| Metric | Source |
|--------|--------|
| App usage duration | UsageStatsManager + AccessibilityService |
| Session start/end | AccessibilityService (TYPE_WINDOW_STATE_CHANGED) |
| Screen unlock count | ACTION_USER_PRESENT broadcast |
| App switch frequency | Session count per day |
| Daily screen time | Sum of all session durations |
| Weekly patterns | Aggregated daily totals |
| Peak usage hour | Hourly event frequency from UsageEvents |

---

## Database Schema

### `app_usage`
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PK | Auto-generated |
| packageName | TEXT | e.g. `com.instagram.android` |
| appName | TEXT | Human-readable name |
| startTime | INTEGER | Unix ms |
| endTime | INTEGER | Unix ms |
| duration | INTEGER | ms |
| date | TEXT | `yyyy-MM-dd` |
| timestamp | INTEGER | Record creation time |

### `sessions`
Same schema as `app_usage` — stores per-session records from AccessibilityService.

### `unlock_events`
| Column | Type |
|--------|------|
| id | INTEGER PK |
| timestamp | INTEGER |
| date | TEXT |

---

## CSV Export Format

```csv
date,app_name,package_name,session_start,session_end,duration_seconds
2024-11-15,"Chrome",com.android.chrome,2024-11-15 09:14:22,2024-11-15 09:27:11,769
2024-11-15,"Instagram",com.instagram.android,2024-11-15 10:02:00,2024-11-15 10:18:30,990
```

---

## Architecture Notes

- **MVVM** — ViewModels expose `StateFlow<UiState>` consumed by Compose screens
- **Repository** — single source of truth, merges Room DB + system APIs
- **Hilt** — constructor injection throughout; no manual DI
- **Room** — all data persisted locally; no network calls
- **Foreground Service** — keeps tracking alive, restarts on boot via `BootReceiver`

---

## Privacy

All data is stored **locally on device**. No data is ever transmitted to any server. The app does not require internet permission.

---

## License

MIT — free for research and personal use.
