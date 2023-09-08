package com.github.sewerina.taxresident.ui

import androidx.navigation.NavHostController

class Nav {
    companion object {
        const val splash = "splash"
        const val home = "home"
        const val record = "record"
        const val recordById = "record/{recordId}"
        const val search = "search"
        const val settings = "settings"
        const val about = "about"
    }
}

fun NavHostController.toHome() {
    this.navigate(Nav.home)
}

fun NavHostController.toSettings() {
    this.navigate(Nav.settings)
}

fun NavHostController.toAbout() {
    this.navigate(Nav.about)
}

// Go to a new record
fun NavHostController.toRecord() {
    this.navigate(Nav.record)
}

// Go to an existed record
fun NavHostController.toRecordById(recordId: Int) {
    this.navigate("record/${recordId}")
}

fun NavHostController.toSearch() {
    this.navigate(Nav.search)
}