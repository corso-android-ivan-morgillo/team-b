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
    val states = MutableLiveData<MainActivityScreenStates>()
    val actions = SingleLiveEvent<MainActivityScreenAction>()

    fun send(event: MainActivityScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is MainActivityScreenEvent.OnSearchClick -> {
                actions.postValue(MainActivityScreenAction.NavigateToSearch(event.query))
            }
            MainActivityScreenEvent.OnMenuClick -> {
                actions.postValue(MainActivityScreenAction.NavigateToSettingMenu)
            }
        }.exhaustive
    }
}

sealed class MainActivityScreenStates {
    object Loading : MainActivityScreenStates()
    data class Error(val error: ErrorStates) : MainActivityScreenStates()
    data class Content(val cocktails: List<SearchCocktailUI>) : MainActivityScreenStates()
}

sealed class MainActivityScreenEvent {
    data class OnSearchClick(val query: String) : MainActivityScreenEvent()
    object OnMenuClick : MainActivityScreenEvent()
}

sealed class MainActivityScreenAction {
    data class NavigateToSearch(val query: String) : MainActivityScreenAction()
    object NavigateToSettingMenu : MainActivityScreenAction()
}
