package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions.ShowNoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.MainScreenActions.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.MainScreenEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Failure
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Success
import kotlinx.coroutines.launch

/* spostiamo la generazione statica della lista all'implementazione della interfaccia */
class MainViewModel(val repository: CocktailRepository) : ViewModel() {

    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenActions>()
    fun send(event: MainScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            // l'activity è pronta
            OnReady -> {
                onReady()
            }
            is MainScreenEvents.OnCocktailClick -> {
                actions.postValue(MainScreenActions.NavigateToDetail(event.cocktail))
            }
        }.exhaustive
    }

    private fun onReady() {
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
            NoCocktailFound -> actions.postValue(ShowNoCocktailFound)
            NoInternet -> actions.postValue(ShowNoInternetMessage)
            ServerError -> actions.postValue(ShowServerError)
            SlowInternet -> actions.postValue(ShowSlowInternet)
        }.exhaustive
    }

    private fun onSuccess(result: Success) {
        val cocktails = result.cocktails.map {
            CocktailUI(
                cocktailName = it.name,
                image = it.image
            )
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
    object Error : MainScreenStates()
    data class Content(val cocktails: List<CocktailUI>) : MainScreenStates()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class MainScreenEvents {
    data class OnCocktailClick(val cocktail: CocktailUI) : MainScreenEvents()
    object OnReady : MainScreenEvents()
}

sealed class MainScreenActions {
    data class NavigateToDetail(val cocktail: CocktailUI) : MainScreenActions()
    object ShowNoInternetMessage : MainScreenActions()
    object ShowNoCocktailFound : MainScreenActions()
    object ShowServerError : MainScreenActions()
    object ShowSlowInternet : MainScreenActions()
}
