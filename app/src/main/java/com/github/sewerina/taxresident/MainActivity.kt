package com.github.sewerina.taxresident

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.sewerina.taxresident.ui.SettingsScreenCallbacks
import com.github.sewerina.taxresident.ui.SettingsViewModel
import com.github.sewerina.taxresident.ui.TaxResidentApp
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()

            val sys = isSystemInDarkTheme()
            val darkThemeState = rememberSaveable {
                mutableStateOf(sys)
            }

            val userName = settingsViewModel.getUserName().collectAsState(initial = "")

            LaunchedEffect(key1 = true) {
                settingsViewModel.getDarkTheme().collect { v ->
                    v?.let { darkThemeState.value = it }
                }
            }

            val settingsScreenCallbacks = SettingsScreenCallbacks(
                onSwitchTheme = {
                    darkThemeState.value = it
                    settingsViewModel.setDarkTheme(it)
                },
                onSaveUserName = {
                    settingsViewModel.setUserName(it)
                },
                onLoadUserName = { userName.value }
            )

            TaxresidentTheme(darkTheme = darkThemeState.value, dynamicColor = false) {
                TaxResidentApp(darkThemeState.value, settingsScreenCallbacks)
            }
        }
    }
}