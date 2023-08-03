package com.github.sewerina.taxresident

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.taxresident.data.RecordDao
import com.github.sewerina.taxresident.data.RecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val recordDao: RecordDao) : ViewModel() {
    val mainViewState = MutableLiveData(MainViewState.initial)

    init {}

    fun load() {
        viewModelScope.launch {
            _load()
        }
    }

    suspend fun _load() {
        delay(1200)
        val list = recordDao.getAll()
        mainViewState.postValue(MainViewState(list = list, loading = false))
    }

    fun getRecord(recordId: Int): RecordEntity {
        mainViewState.value?.let {
            val f = it.list.firstOrNull { r -> r.id == recordId }
            if (f != null) {
                return f
            }
        }
        return RecordEntity(0, null, null, "")
    }

    fun save(entity: RecordEntity) {
        mainViewState.value?.let { state ->
            mainViewState.value = MainViewState(list = state.list, loading = true)
        }
        viewModelScope.launch {
            if (entity.id > 0) {
                recordDao.update(entity)
            } else {
                recordDao.add(entity)
            }
            _load()
        }
    }

    fun delete(entity: RecordEntity) {
        mainViewState.value?.let { state ->
            val newList = state.list.toMutableList()
            newList.remove(entity)
            mainViewState.value = MainViewState(list = newList, loading = true)
        }
        viewModelScope.launch {
            if (entity.id > 0) {
//                Log.i("VM", "delete: ${entity.id}")
                recordDao.delete(entity)
                _load()
            }
        }
    }
}

class MainViewState(val list: List<RecordEntity>, val loading: Boolean) {
    var usedDays = 0L
    var leftDays = 0L

    init {
        calculateDays()
    }

    companion object {
        const val LIMIT_DAYS = 182L
        val initial = MainViewState(list = emptyList(), loading = true)
    }

    private fun calculateDays() {
        val yearAgo = Instant.now().minus(365, ChronoUnit.DAYS)
        for (entity: RecordEntity in list) {
            usedDays += entity.days(yearAgo)
        }

        leftDays = LIMIT_DAYS - usedDays
    }
}