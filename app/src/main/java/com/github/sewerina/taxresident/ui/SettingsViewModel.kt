package com.github.sewerina.taxresident.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    fun setUserName(name: String) {
        viewModelScope.launch {
            dataStoreManager.setUserName(name)
        }
    }

    fun getUserName(): Flow<String> {
        return dataStoreManager.getUserName()
    }

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDarkTheme(value)
        }
    }

    fun getDarkTheme(): Flow<Boolean?> {
        return dataStoreManager.getDarkTheme()
    }
}