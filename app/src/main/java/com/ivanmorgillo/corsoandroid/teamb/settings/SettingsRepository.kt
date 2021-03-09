package com.ivanmorgillo.corsoandroid.teamb.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SettingsRepository {
    suspend fun saveThemeSwitch(themeSwitchOn: Boolean): Boolean
    suspend fun saveScreenSwitch(screenSwitchOn: Boolean): Boolean
    suspend fun isThemeSwitchOn(): Boolean
    suspend fun isScreenSwitchOn(): Boolean
}

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {
    private val storage: SharedPreferences by lazy {
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }

    override suspend fun saveThemeSwitch(themeSwitchOn: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("theme", themeSwitchOn).commit()
    }

    override suspend fun saveScreenSwitch(screenSwitchOn: Boolean) = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("screen", screenSwitchOn).commit()
    }

    override suspend fun isThemeSwitchOn(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("theme", false)
    }

    override suspend fun isScreenSwitchOn(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("screen", true)
    }
}
