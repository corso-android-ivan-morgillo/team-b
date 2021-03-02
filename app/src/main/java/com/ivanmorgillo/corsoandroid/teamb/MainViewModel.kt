package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.search.SearchCocktailUI
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import timber.log.Timber

class MainActivityViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking

) : ViewModel() {
    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    fun send(event: MainScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is MainScreenEvent.OnSearchClick -> {
                actions.postValue(MainScreenAction.NavigateToSearch(event.query))
            }
            MainScreenEvent.OnMenuClick -> {
                actions.postValue(MainScreenAction.NavigateToSettingMenu)
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
}

sealed class MainScreenAction {
    data class NavigateToSearch(val query: String) : MainScreenAction()
    object NavigateToSettingMenu : MainScreenAction()
}
