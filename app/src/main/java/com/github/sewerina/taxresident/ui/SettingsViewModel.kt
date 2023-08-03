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
//    companion object {
//        private const val USER_NAME = "User name"
//        private const val DARK_THEME = "Dark theme"
//    }

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

//    fun setUserName(name: String) {
//        sharedPreferences.edit().putString(USER_NAME, name).apply()
//    }
//
//    fun getUserName(): String {
//        if (sharedPreferences.contains(USER_NAME)) {
//            return sharedPreferences.getString(USER_NAME, "Пользователь")!!
//        }
//        return "Пользователь"
//    }
//
//    fun setDarkTheme(value: Boolean) {
//        sharedPreferences.edit().putBoolean(DARK_THEME, value).apply()
//    }
//
//    fun getDarkTheme(): Boolean? {
//        if (sharedPreferences.contains(DARK_THEME)) {
//            return sharedPreferences.getBoolean(DARK_THEME, false)
//        }
//        return null
//    }
}