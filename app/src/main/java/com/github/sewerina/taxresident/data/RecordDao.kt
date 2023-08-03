package com.github.sewerina.taxresident.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {
    // Get all records and see the latest date first and the earliest date last, need to sort in descending order (DESC).
    @Query("SELECT * FROM record ORDER BY departure_date DESC")
    suspend fun getAll(): List<RecordEntity>

    @Insert
    suspend fun add(recordEntity: RecordEntity)

    @Update()
    suspend fun update(recordEntity: RecordEntity)

    @Delete
    suspend fun delete(recordEntity: RecordEntity)
}