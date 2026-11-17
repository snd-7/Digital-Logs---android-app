package com.digitallogs.di

import android.content.Context
import com.digitallogs.data.database.AppUsageDao
import com.digitallogs.data.database.DigitalLogsDatabase
import com.digitallogs.data.database.SessionDao
import com.digitallogs.data.database.UnlockEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DigitalLogsDatabase =
        DigitalLogsDatabase.getInstance(context)

    @Provides
    fun provideAppUsageDao(db: DigitalLogsDatabase): AppUsageDao = db.appUsageDao()

    @Provides
    fun provideSessionDao(db: DigitalLogsDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideUnlockEventDao(db: DigitalLogsDatabase): UnlockEventDao = db.unlockEventDao()
}
