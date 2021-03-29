package com.ivanmorgillo.corsoandroid.teamb

import android.content.DialogInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.CancelClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.DisableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.EnableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFavorite
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToFeedBack
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToHome
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToInstagram
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToRandom
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSearch
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToSettingMenu
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.NavigateToTwitter
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignIn
import com.ivanmorgillo.corsoandroid.teamb.MainScreenAction.SignOut
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.AfterSignOut
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnCancelClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnCustomClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnFavoriteClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnFeedBackClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnInstagramClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnMenuClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnRandomClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnSearchClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnSignInClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnSignOutClick
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvent.OnTwitterClick
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
    private val tracking: Tracking,
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
            OnInstagramClick -> {
                tracking.logEvent("instagram_clicked")
                actions.postValue(NavigateToInstagram)
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
            OnSignOutClick -> {
                tracking.logEvent("sign_out_click_navigation_drawer")
                actions.postValue(SignOut)
            }
            is OnCancelClick -> {
                tracking.logEvent("cancel_click_dialog_alert")
                actions.postValue(CancelClick(event.dialog))
            }
            AfterSignOut -> actions.postValue(NavigateToHome)
            OnCustomClick -> {
                tracking.logEvent("custom_drink_clicked")
                actions.postValue(MainScreenAction.NavigateToCustom)
            }
            MainScreenEvent.OnCustomListClick -> {
                tracking.logEvent("custom_drink_list")
                actions.postValue(MainScreenAction.NavigateToCustomList)
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
    object OnInstagramClick : MainScreenEvent()
    object OnTwitterClick : MainScreenEvent()
    object OnFeedBackClick : MainScreenEvent()
    object OnFavoriteClick : MainScreenEvent()
    object OnRandomClick : MainScreenEvent()
    object OnSignInClick : MainScreenEvent()
    object OnSignOutClick : MainScreenEvent()
    object AfterSignOut : MainScreenEvent()
    data class OnCancelClick(val dialog: DialogInterface) : MainScreenEvent()
    object OnCustomListClick : MainScreenEvent()
    object OnCustomClick : MainScreenEvent()
}

sealed class MainScreenAction {
    data class NavigateToSearch(val query: String) : MainScreenAction()
    object NavigateToSettingMenu : MainScreenAction()
    object NavigateToInstagram : MainScreenAction()
    object NavigateToTwitter : MainScreenAction()
    object NavigateToFeedBack : MainScreenAction()
    object EnableDarkMode : MainScreenAction()
    object DisableDarkMode : MainScreenAction()
    object NavigateToFavorite : MainScreenAction()
    object NavigateToRandom : MainScreenAction()
    object SignIn : MainScreenAction()
    object SignOut : MainScreenAction()
    object NavigateToHome : MainScreenAction()
    data class CancelClick(val dialog: DialogInterface) : MainScreenAction()
    object NavigateToCustomList : MainScreenAction()
    object NavigateToCustom : MainScreenAction()
}
