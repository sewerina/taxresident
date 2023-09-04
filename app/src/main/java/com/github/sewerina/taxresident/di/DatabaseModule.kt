package com.github.sewerina.taxresident.di

import android.content.Context
import androidx.room.Room
import com.github.sewerina.taxresident.data.AppDatabase
import com.github.sewerina.taxresident.data.RecordDao
import com.github.sewerina.taxresident.data.SuggestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "Records"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRecordDao(appDatabase: AppDatabase): RecordDao = appDatabase.recordDao()

    @Provides
    fun provideSuggestionDao(appDatabase: AppDatabase): SuggestionDao = appDatabase.suggestionDao()
}