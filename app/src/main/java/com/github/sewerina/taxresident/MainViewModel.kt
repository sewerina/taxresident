package com.github.sewerina.taxresident

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sewerina.taxresident.data.RecordDao
import com.github.sewerina.taxresident.data.RecordEntity
import com.github.sewerina.taxresident.data.SuggestionDao
import com.github.sewerina.taxresident.data.SuggestionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recordDao: RecordDao,
    private val suggestionDao: SuggestionDao
) : ViewModel() {
    val mainViewState = MutableLiveData(MainViewState.initial)
    val searchViewState = MutableLiveData(SearchViewState.initial)

    init {}

    fun load() {
        viewModelScope.launch {
            _load()
        }
    }

    suspend fun _load() {
        delay(1200)
        val recordList = recordDao.getAll()
        mainViewState.postValue(MainViewState(list = recordList, loading = false))
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
                recordDao.delete(entity)
                _load()
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            searchViewState.postValue(
                SearchViewState(
                    recordList = emptyList(),
                    suggestionList = emptyList(),
                    query = "",
                    loading = true
                )
            )
            val searchList = recordDao.search("%${query}%")

            suggestionDao.delete(query)
            suggestionDao.add(SuggestionEntity(id = 0, suggestion = query, usedDate = Date().time))
            val suggestionList = suggestionDao.getAll().map { it.suggestion }

            searchViewState.postValue(
                SearchViewState(
                    recordList = searchList,
                    suggestionList = suggestionList,
                    query = query,
                    loading = false
                )
            )
        }
    }

    fun initSuggestionList() {
        viewModelScope.launch {
            searchViewState.postValue(
                SearchViewState(
                    recordList = emptyList(),
                    suggestionDao.getAll().map { it.suggestion },
                    query = "",
                    loading = false
                )
            )
        }
    }

    fun clearSearch() {
        viewModelScope.launch {
            searchViewState.postValue(
                SearchViewState(
                    recordList = emptyList(),
                    suggestionList = searchViewState.value?.suggestionList ?: emptyList(),
                    query = "",
                    loading = false
                )
            )
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

class SearchViewState(
    val recordList: List<RecordEntity>,
    val suggestionList: List<String>,
    val query: String,
    val loading: Boolean
) {
    companion object {
        val initial = SearchViewState(
            recordList = emptyList(),
            suggestionList = emptyList(),
            query = "",
            loading = false
        )
    }
}