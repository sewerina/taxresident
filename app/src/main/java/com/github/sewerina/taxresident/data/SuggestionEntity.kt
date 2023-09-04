package com.github.sewerina.taxresident.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestion")
data class SuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "suggestion") val suggestion: String,
    @ColumnInfo(name = "used_date") val usedDate: Long
)