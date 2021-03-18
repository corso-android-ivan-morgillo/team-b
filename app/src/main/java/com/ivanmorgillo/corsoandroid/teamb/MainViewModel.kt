package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.DisableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.EnableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFacebook
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFavorite
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFeedBack
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToRandom
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSearch
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSettingMenu
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToTwitter
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignIn
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignOut
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnFacebookClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnFavoriteClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnFeedBackClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnMenuClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnRandomClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnSearchClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnSignInClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnTwitterClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.UserLogged
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
                actions.postValue(EnableDarkMode)
            } else {
                actions.postValue(DisableDarkMode)
            }
        }
    }

    fun send(event: MainScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is OnSearchClick -> {
                tracking.logEvent("search_clicked")
                actions.postValue(NavigateToSearch(event.query))
            }
            OnMenuClick -> {
                tracking.logEvent("settings_clicked")
                actions.postValue(NavigateToSettingMenu)
            }
            OnFacebookClick -> {
                tracking.logEvent("facebook_clicked")
                actions.postValue(NavigateToFacebook)
            }
            OnTwitterClick -> {
                tracking.logEvent("twitter_clicked")
                actions.postValue(NavigateToTwitter)
            }
            OnFeedBackClick -> {
                tracking.logEvent("feedback_clicked")
                actions.postValue(NavigateToFeedBack)
            }
            OnFavoriteClick -> {
                tracking.logEvent("favorite_list_clicked")
                actions.postValue(NavigateToFavorite)
            }
            OnRandomClick -> {
                tracking.logEvent("random_clicked")
                actions.postValue(NavigateToRandom)
            }
            OnSignInClick -> {
                tracking.logEvent("sign_in_click_navigation_drawer")
                actions.postValue(SignIn)
            }
            UserLogged -> TODO()
            MainScreenEvent.OnSignOutClick -> {
                tracking.logEvent("sign_out_click_navigation_drawer")
                actions.postValue(SignOut)
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
    object OnFavoriteClick : MainScreenEvent()
    object OnRandomClick : MainScreenEvent()
    object OnSignInClick : MainScreenEvent()
    object UserLogged : MainScreenEvent()
    object OnSignOutClick : MainScreenEvent()
}

sealed class MainScreenAction {
    data class NavigateToSearch(val query: String) : MainScreenAction()
    object NavigateToSettingMenu : MainScreenAction()
    object NavigateToFacebook : MainScreenAction()
    object NavigateToTwitter : MainScreenAction()
    object NavigateToFeedBack : MainScreenAction()
    object EnableDarkMode : MainScreenAction()
    object DisableDarkMode : MainScreenAction()
    object NavigateToFavorite : MainScreenAction()
    object NavigateToRandom : MainScreenAction()
    object SignIn : MainScreenAction()
    object SignOut : MainScreenAction()
}
