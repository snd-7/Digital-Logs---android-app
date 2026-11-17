package com.digitallogs.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.digitallogs.data.models.AppUsageEntity
import com.digitallogs.data.models.SessionEntity
import com.digitallogs.data.models.UnlockEventEntity

@Database(
    entities = [AppUsageEntity::class, SessionEntity::class, UnlockEventEntity::class],
    version = 2,          // bumped from 1 → 2 to apply new unique indices
    exportSchema = false
)
abstract class DigitalLogsDatabase : RoomDatabase() {

    abstract fun appUsageDao(): AppUsageDao
    abstract fun sessionDao(): SessionDao
    abstract fun unlockEventDao(): UnlockEventDao

    companion object {
        @Volatile private var INSTANCE: DigitalLogsDatabase? = null

        fun getInstance(context: Context): DigitalLogsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DigitalLogsDatabase::class.java,
                    "digital_logs_db"
                )
                .fallbackToDestructiveMigration()   // wipes old DB on version bump
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
