package com.ivanmorgillo.corsoandroid.teamb.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnScreenSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnThemeSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingsRepository: SettingsRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<SettingScreenStates>()

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
