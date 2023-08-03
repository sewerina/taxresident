package com.github.sewerina.taxresident.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.Instant

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "departure_date") val departureDate: Long?,
    @ColumnInfo(name = "arrival_date") val arrivalDate: Long?,
    @ColumnInfo(name = "comment") val comment: String?
) {
    fun days(yearAgo: Instant): Long {
        if (departureDate == null) return 0L
        var departureInstant = Instant.ofEpochMilli(departureDate)
        val arrivalInstant =
            if (arrivalDate == null) Instant.now() else Instant.ofEpochMilli(arrivalDate)
        if (arrivalInstant.isBefore(yearAgo)) return 0L
        if (departureInstant.isBefore(yearAgo)) departureInstant = yearAgo
        val duration = Duration.between(departureInstant, arrivalInstant)
        return duration.toDays()
    }

    fun days(): Long {
        if (departureDate == null) return 0L
        val departureInstant = Instant.ofEpochMilli(departureDate)
        val arrivalInstant =
            if (arrivalDate == null) Instant.now() else Instant.ofEpochMilli(arrivalDate)
        val duration = Duration.between(departureInstant, arrivalInstant)
        return duration.toDays()
    }
}