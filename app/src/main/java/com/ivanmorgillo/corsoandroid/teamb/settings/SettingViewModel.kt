package com.ivanmorgillo.corsoandroid.teamb.settings

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.home.GeneralContent
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnScreenSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingScreenEvents.OnThemeSwitchClick
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking

class SettingViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<SettingScreenStates>()
    val actions = SingleLiveEvent<SettingScreenActions>()

    init {
        tracking.logScreen(Screens.Setting)
    }

    fun send(event: SettingScreenEvents) {
        when (event) {
            is OnThemeSwitchClick -> {
                tracking.logEvent("settings_on_theme_switch_click")
                actions.postValue(SettingScreenActions.ChangeTheme(event.isChecked))
            }
            is OnScreenSwitchClick -> {
                tracking.logEvent("settings_on_screen_switch_click")
                actions.postValue(SettingScreenActions.ChangeScreen(event.isChecked, event.editor))
            }
        }
    }
}

sealed class SettingScreenEvents {
    data class OnThemeSwitchClick(val isChecked: Boolean) : SettingScreenEvents()
    data class OnScreenSwitchClick(
        val isChecked: Boolean,
        val editor: SharedPreferences.Editor?
    ) : SettingScreenEvents()
}

sealed class SettingScreenActions {
    data class ChangeTheme(val isChecked: Boolean) : SettingScreenActions()
    data class ChangeScreen(val isChecked: Boolean, val editor: SharedPreferences.Editor?) : SettingScreenActions()
}

sealed class SettingScreenStates {
    object Loading : SettingScreenStates()
    data class Error(val error: ErrorStates) : SettingScreenStates()
    data class Content(val generalContent: GeneralContent) : SettingScreenStates()
}
