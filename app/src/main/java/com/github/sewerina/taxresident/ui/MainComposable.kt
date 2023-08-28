package com.github.sewerina.taxresident.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.sewerina.taxresident.MainViewModel
import com.github.sewerina.taxresident.MainViewState
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.data.RecordEntity

@Composable
fun TaxResidentApp(darkTheme: Boolean, settingsScreenCallbacks: SettingsScreenCallbacks) {
    val navController = rememberNavController()
    settingsScreenCallbacks.onBack = { navController.popBackStack() }
    TaxResidentNavHost(
        darkTheme = darkTheme,
        settingsScreenCallbacks = settingsScreenCallbacks,
        navController = navController
    )
}

@Composable
fun TaxResidentNavHost(
    darkTheme: Boolean,
    settingsScreenCallbacks: SettingsScreenCallbacks,
    navController: NavHostController
) {
    val vm: MainViewModel = viewModel()
    val state = vm.mainViewState.observeAsState(initial = MainViewState.initial)

    NavHost(navController = navController, startDestination = Nav.splash) {
        composable(Nav.splash) {
            val helloDrawables = listOf(
                R.drawable.hello_1,
                R.drawable.hello_2,
                R.drawable.hello_3,
                R.drawable.hello_4,
                R.drawable.hello_5
            )
            val randomDraw = helloDrawables.random()
            AnimatedSplashScreen(
                randomDraw,
                settingsScreenCallbacks.onLoadUserName.invoke(),
                navController
            )
        }
        composable(Nav.home) {
            HomeScreen(
                state,
                HomeScreenCallbacks(
                    onNewRecord = { navController.toRecord() },
                    onEdit = { recordId -> navController.toRecordById(recordId) },
                    onRemove = { record -> vm.delete(record) },
                    onSearch = { navController.toSearch() },
                    onSettings = { navController.toSettings() },
                    onAbout = { navController.toAbout() })
            )
        }
        composable(Nav.search) {
            SearchScreen(vm, SearchScreenCallbacks(
                onEdit = { recordId -> navController.toRecordById(recordId) },
                onRemove = { record -> vm.delete(record) },
                onBack = { navController.popBackStack() }
            ))
        }
        composable(Nav.settings) {
            SettingsScreen(
                darkTheme = darkTheme,
                callbacks = settingsScreenCallbacks
            )
        }
        composable(Nav.about) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Nav.record) {
            // click on the plus button for add new record
            val record = RecordEntity(0, null, null, "")
            RecordScreen(
                record,
                list = state.value.list,
                callbacks = RecordScreenCallbacks(
                    onBack = { navController.popBackStack() },
                    onSave = { entity ->
                        vm.save(entity)
                        navController.popBackStack()
                    },
                    onDelete = { }
                )
            )
        }
        composable(
            Nav.recordById,
            arguments = listOf(navArgument("recordId") { type = NavType.IntType })
        ) {
            // click on the interest record in the list
            val recordId = it.arguments?.getInt("recordId")
            val record =
                if (recordId == null) RecordEntity(0, null, null, "") else vm.getRecord(recordId)
            RecordScreen(
                record,
                list = state.value.list,
                callbacks = RecordScreenCallbacks(
                    onBack = { navController.popBackStack() },
                    onSave = { entity ->
                        vm.save(entity)
                        navController.popBackStack()
                    },
                    onDelete = { entity ->
                        vm.delete(entity)
                        navController.popBackStack()
                    }
                )
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        vm.load()
    }
}

fun Long?.show(): String {
    if (this == null) {
        return ""
    }

    val pattern = DateFormat.format("dd.MM.yyyy", this)
    return pattern.toString()
}

fun String?.show(): String {
    if (this == null) {
        return ""
    }

    return this
}