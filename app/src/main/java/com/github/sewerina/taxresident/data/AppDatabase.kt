package com.github.sewerina.taxresident.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecordEntity::class, SuggestionEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    abstract fun suggestionDao(): SuggestionDao
}