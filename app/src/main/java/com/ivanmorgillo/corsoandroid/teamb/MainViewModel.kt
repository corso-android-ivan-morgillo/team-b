package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.search.SearchCocktailUI
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingsRepository
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel(
    private val settingsrepository: SettingsRepository,
    private val tracking: Tracking

) : ViewModel() {
    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    init {
        viewModelScope.launch {
            val isDarkModeOn = settingsrepository.isThemeSwitchOn()
            if (isDarkModeOn) {
                actions.postValue(MainScreenAction.EnableDarkMode)
            } else {
                actions.postValue(MainScreenAction.DisableDarkMode)
            }
        }
    }

    fun send(event: MainScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is MainScreenEvent.OnSearchClick -> {
                tracking.logEvent("search_clicked")
                actions.postValue(MainScreenAction.NavigateToSearch(event.query))
            }
            MainScreenEvent.OnMenuClick -> {
                tracking.logEvent("settings_clicked")
                actions.postValue(MainScreenAction.NavigateToSettingMenu)
            }
            MainScreenEvent.OnFacebookClick -> {
                tracking.logEvent("facebook_clicked")
                actions.postValue(MainScreenAction.NavigateToFacebook)
            }
            MainScreenEvent.OnTwitterClick -> {
                tracking.logEvent("twitter_clicked")
                actions.postValue(MainScreenAction.NavigateToTwitter)
            }
            MainScreenEvent.OnFeedBackClick -> {
                tracking.logEvent("feedback_clicked")
                actions.postValue(MainScreenAction.NavigateToFeedBack)
            }
        }.exhaustive
    }
}

sealed class MainScreenStates {
    object Loading : MainScreenStates()
    data class Error(val error: ErrorStates) : MainScreenStates()
    data class Content(val cocktails: List<SearchCocktailUI>) : MainScreenStates()
}

sealed class MainScreenEvent {
    data class OnSearchClick(val query: String) : MainScreenEvent()
    object OnMenuClick : MainScreenEvent()
    object OnFacebookClick : MainScreenEvent()
    object OnTwitterClick : MainScreenEvent()
    object OnFeedBackClick : MainScreenEvent()
}

sealed class MainScreenAction {
    data class NavigateToSearch(val query: String) : MainScreenAction()
    object NavigateToSettingMenu : MainScreenAction()
    object NavigateToFacebook : MainScreenAction()
    object NavigateToTwitter : MainScreenAction()
    object NavigateToFeedBack : MainScreenAction()
    object EnableDarkMode : MainScreenAction()
    object DisableDarkMode : MainScreenAction()
}
