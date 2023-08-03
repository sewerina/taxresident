package com.github.sewerina.taxresident

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            val vm: SettingsViewModel = viewModel()

            val sys = isSystemInDarkTheme()
            val darkThemeState = rememberSaveable {
                mutableStateOf(sys)
            }
//            val darkThemeState = vm.getDarkTheme().collectAsState(initial = sys)

//            val userName = rememberSaveable {
//                mutableStateOf("")
//            }
            val userName = vm.getUserName().collectAsState(initial = "")

            LaunchedEffect(key1 = true) {
                vm.getDarkTheme().collect { v ->
                    v?.let { darkThemeState.value = it }
                }

//                vm.getUserName().collect { v ->
//                    userName.value = v
//                }
            }

            val settingsScreenCallbacks = SettingsScreenCallbacks(
                onSwitchTheme = {
                    darkThemeState.value = it
                    vm.setDarkTheme(it)
                },
                onSaveUserName = {
//                    userName.value = it
                    vm.setUserName(it)
                },
                onLoadUserName = { userName.value }
            )

            TaxresidentTheme(darkTheme = darkThemeState.value, dynamicColor = false) {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//
//                }
                TaxResidentApp(darkThemeState.value, settingsScreenCallbacks)
            }
        }
    }
}