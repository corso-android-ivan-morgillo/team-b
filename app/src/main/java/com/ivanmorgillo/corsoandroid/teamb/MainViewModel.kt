package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Failure
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Success
import kotlinx.coroutines.launch
import timber.log.Timber

/* spostiamo la generazione statica della lista all'implementazione della interfaccia */
class MainViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking

) : ViewModel() {
    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenActions>()
    fun send(event: MainScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            // l'activity è pronta
            OnReady -> {
                loadContent()
            }
            is MainScreenEvents.OnCocktailClick -> {
                tracking.logEvent("Cocktail_Clicked")
                actions.postValue(MainScreenActions.NavigateToDetail(event.cocktail))
            }
            MainScreenEvents.OnRefreshClicked -> {
                // add tracking
                loadContent()
            }
            MainScreenEvents.OnSettingClick -> {
                actions.postValue(MainScreenActions.NavigateToSettings)
            }
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(MainScreenStates.Loading)
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
            NoCocktailFound -> states.postValue(MainScreenStates.Error(ErrorStates.ShowNoCocktailFound))
            NoInternet -> states.postValue(MainScreenStates.Error(ErrorStates.ShowNoInternetMessage))
            ServerError -> states.postValue(MainScreenStates.Error(ErrorStates.ShowServerError))
            SlowInternet -> states.postValue(MainScreenStates.Error(ErrorStates.ShowSlowInternet))
        }.exhaustive
    }

    private fun onSuccess(result: Success) {
        val cocktails = result.cocktails.map {
            CocktailUI(cocktailName = it.name, image = it.image, id = it.idDrink)
        }
        states.postValue(MainScreenStates.Content(cocktails))
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class MainScreenStates {
    object Loading : MainScreenStates()
    data class Error(val error: ErrorStates) : MainScreenStates()
    data class Content(val cocktails: List<CocktailUI>) : MainScreenStates()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class MainScreenEvents {
    data class OnCocktailClick(val cocktail: CocktailUI) : MainScreenEvents()
    object OnReady : MainScreenEvents()
    object OnRefreshClicked : MainScreenEvents()
    object OnSettingClick : MainScreenEvents()
}

sealed class MainScreenActions {
    data class NavigateToDetail(val cocktail: CocktailUI) : MainScreenActions()
    object NavigateToSettings : MainScreenActions()
}

sealed class ErrorStates {
    object ShowNoInternetMessage : ErrorStates()
    object ShowNoCocktailFound : ErrorStates()
    object ShowServerError : ErrorStates()
    object ShowSlowInternet : ErrorStates()
}
