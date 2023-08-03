package com.github.sewerina.taxresident.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tax_resident_settings")

class DataStoreManager(val context: Context) {
    companion object {
        private const val USER_NAME = "User name"
        private const val DARK_THEME = "Dark theme"
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(USER_NAME)] = name
        }
    }

    fun getUserName(): Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[stringPreferencesKey(USER_NAME)] ?: "Пользователь"
        }


    suspend fun setDarkTheme(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(DARK_THEME)] = value
        }
    }

    fun getDarkTheme(): Flow<Boolean?> =
        context.dataStore.data.map { prefs ->
            prefs[booleanPreferencesKey(DARK_THEME)]
        }
}