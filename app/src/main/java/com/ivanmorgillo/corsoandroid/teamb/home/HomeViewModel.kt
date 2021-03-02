package com.ivanmorgillo.corsoandroid.teamb.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.CocktailUI
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Failure
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Success
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

/* spostiamo la generazione statica della lista all'implementazione della interfaccia */
class HomeViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking

) : ViewModel() {
    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<HomeScreenStates>()
    val actions = SingleLiveEvent<HomeScreenActions>()
    fun send(event: HomeScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            // l'activity è pronta
            HomeScreenEvents.OnReady -> {
                loadContent()
            }
            is HomeScreenEvents.OnCocktailClick -> {
                tracking.logEvent("Cocktail_Clicked")
                actions.postValue(HomeScreenActions.NavigateToDetail(event.cocktail))
            }
            HomeScreenEvents.OnRefreshClicked -> {
                // add tracking
                loadContent()
            }
            HomeScreenEvents.OnSettingClick -> {
                actions.postValue(HomeScreenActions.NavigateToSettings)
            }
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(HomeScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadCocktails()
            when (result) {
                is Failure -> onFailure(result)
                is Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onFailure(result: Failure) {
        when (result.error) {
            NoCocktailFound -> states.postValue(HomeScreenStates.Error(ErrorStates.ShowNoCocktailFound))
            NoInternet -> states.postValue(HomeScreenStates.Error(ErrorStates.ShowNoInternetMessage))
            ServerError -> states.postValue(HomeScreenStates.Error(ErrorStates.ShowServerError))
            SlowInternet -> states.postValue(HomeScreenStates.Error(ErrorStates.ShowSlowInternet))
        }.exhaustive
    }

    private fun onSuccess(result: Success) {
        val cocktails = result.cocktails.map {
            CocktailUI(cocktailName = it.name, image = it.image, id = it.idDrink)
        }
        states.postValue(HomeScreenStates.Content(cocktails))
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    data class Error(val error: ErrorStates) : HomeScreenStates()
    data class Content(val cocktails: List<CocktailUI>) : HomeScreenStates()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class HomeScreenEvents {
    data class OnCocktailClick(val cocktail: CocktailUI) : HomeScreenEvents()
    object OnReady : HomeScreenEvents()
    object OnRefreshClicked : HomeScreenEvents()
    object OnSettingClick : HomeScreenEvents()
}

sealed class HomeScreenActions {
    data class NavigateToDetail(val cocktail: CocktailUI) : HomeScreenActions()
    object NavigateToSettings : HomeScreenActions()
}

sealed class ErrorStates {
    object ShowNoInternetMessage : ErrorStates()
    object ShowNoCocktailFound : ErrorStates()
    object ShowServerError : ErrorStates()
    object ShowSlowInternet : ErrorStates()
}
