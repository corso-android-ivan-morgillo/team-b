package com.ivanmorgillo.corsoandroid.teamb.utils

import android.app.UiModeManager
import android.view.View
import androidx.appcompat.app.AppCompatDelegate

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun enableDarkMode() {
    AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_YES)
}

fun disableDarkMode() {
    AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_NO)
}
