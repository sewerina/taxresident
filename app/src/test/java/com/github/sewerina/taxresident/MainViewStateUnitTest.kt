package com.github.sewerina.taxresident

import com.github.sewerina.taxresident.data.RecordEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainViewStateUnitTest {
    @Test
    fun calculateDaysForEmptyList() {
        val recordList = listOf<RecordEntity>()
        val mainViewState = MainViewState(recordList, false)
        assertEquals(0, mainViewState.usedDays)
        assertEquals(182, mainViewState.leftDays)
    }

    @Test
    fun calculateDaysForNotEmptyList() {
        val dep = Instant.now().minus(280, ChronoUnit.DAYS).toEpochMilli()
        val arr = Instant.now().minus(230, ChronoUnit.DAYS).toEpochMilli()
        val record = RecordEntity(0, dep, arr, "test")
        val mainViewState = MainViewState(listOf(record), false)
        assertEquals(50, mainViewState.usedDays)
        assertEquals(182 - 50, mainViewState.leftDays)
    }

    @Test
    fun `calculateDays игнорирует записи старше года`() {
        val dep = Instant.now().minus(450, ChronoUnit.DAYS).toEpochMilli()
        val arr = Instant.now().minus(420, ChronoUnit.DAYS).toEpochMilli()
        val record = RecordEntity(1, dep, arr, "test")
        val mainViewState = MainViewState(listOf(record), false)
        assertEquals(0, mainViewState.usedDays)
    }

    @Test
    fun `calculateDays запись когда dep_date старше года`() {
        val now = Instant.now()
        val dep = now.minus(400, ChronoUnit.DAYS).toEpochMilli()
        val arr = now.minus(230, ChronoUnit.DAYS).toEpochMilli()
        val record = RecordEntity(1, dep, arr, "test")
        val mainViewState = MainViewState(listOf(record), false)
        assertEquals(364 - 230, mainViewState.usedDays)
    }
}