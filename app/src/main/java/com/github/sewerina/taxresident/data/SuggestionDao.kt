package com.github.sewerina.taxresident.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SuggestionDao {
    @Query("SELECT * FROM suggestion ORDER BY used_date DESC")
    suspend fun getAll(): List<SuggestionEntity>

    @Query("DELETE FROM suggestion WHERE suggestion.suggestion=:suggestion")
    suspend fun delete(suggestion: String)

    @Insert
    suspend fun add(entity: SuggestionEntity)
}