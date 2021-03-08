package com.ivanmorgillo.corsoandroid.teamb.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnScreenSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnThemeSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

class SettingViewModel(
    private val settingsRepository: SettingsRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<SettingScreenStates>()
    val actions = SingleLiveEvent<SettingScreenActions>()

    init {
        tracking.logScreen(Screens.Setting)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: SettingScreenEvents) {
        when (event) {
            is OnThemeSwitchClick -> {
                onThemeSwitchClicked(event)
            }
            is OnScreenSwitchClick -> {
                onScreenSwitchClicked(event)
            }
            SettingScreenEvents.OnReady -> {
                viewModelScope.launch {
                    val themeSwitchOn = settingsRepository.isThemeSwitchOn()
                    val screenSwitchOn = settingsRepository.isScreenSwitchOn()
                    val content = Content(
                        metricSystemOn = false,
                        screenActiveOn = screenSwitchOn,
                        changeThemeOn = themeSwitchOn,
                        language = SupportedLanguages.Italian
                    )
                    states.postValue(content)
                }
            }
        }.exhaustive
    }

    private fun onScreenSwitchClicked(event: OnScreenSwitchClick) {
        tracking.logEvent("settings_on_screen_switch_click")
        val screenSwitchOn = event.isChecked
        viewModelScope.launch {
            settingsRepository.saveScreenSwitch(screenSwitchOn)
        }
        val currentContent = states.value
        if (currentContent != null && currentContent is Content) {
            val updatedStates = currentContent.copy(
                screenActiveOn = screenSwitchOn
            )
            states.postValue(updatedStates)
        }
    }

    private fun onThemeSwitchClicked(event: OnThemeSwitchClick) {
        tracking.logEvent("settings_on_theme_switch_click")
        val themeSwitchOn = event.isChecked
        viewModelScope.launch {
            settingsRepository.saveThemeSwitch(themeSwitchOn)
        }
        val currentContent = states.value
        if (currentContent != null && currentContent is Content) {
            val updatedStates = currentContent.copy(
                changeThemeOn = themeSwitchOn
            )
            states.postValue(updatedStates)
        }
    }
}

sealed class SettingScreenEvents {
    data class OnThemeSwitchClick(val isChecked: Boolean) : SettingScreenEvents()
    data class OnScreenSwitchClick(
        val isChecked: Boolean
    ) : SettingScreenEvents()

    object OnReady : SettingScreenEvents()
}

sealed class SettingScreenActions {
}

sealed class SettingScreenStates {
    object Loading : SettingScreenStates()
    data class Error(val error: ErrorStates) : SettingScreenStates()
    data class Content(
        val metricSystemOn: Boolean,
        val screenActiveOn: Boolean,
        val changeThemeOn: Boolean,
        val language: SupportedLanguages
    ) : SettingScreenStates()
}

sealed class SupportedLanguages {
    object Italian : SupportedLanguages()
    object English : SupportedLanguages()
}
