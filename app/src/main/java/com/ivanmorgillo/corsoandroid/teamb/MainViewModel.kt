package com.ivanmorgillo.corsoandroid.teamb

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            MainScreenEvents.OnReady -> {
                viewModelScope.launch {
                    val cocktails = repository.loadCocktails().map {
                        CocktailUI(
                            cocktailName = it.name,
                            image = it.image
                        )
                    }
                    states.postValue(MainScreenStates.Content(cocktails))
                }
            }
            is MainScreenEvents.OnCocktailClick -> {
                Log.d("COCKTAIL", event.cocktail.toString())
                actions.postValue(MainScreenActions.NavigateToDetail(event.cocktail))
            }
        }.exhaustive
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
}
