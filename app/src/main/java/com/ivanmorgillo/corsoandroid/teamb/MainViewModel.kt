package com.ivanmorgillo.corsoandroid.teamb

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val MAXRANGE = 10

class MainViewModel : ViewModel() {
    private val cocktail_name = "Mojito"
    private val imageCocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
    private val cocktailList = (1..MAXRANGE).map {
        CocktailUI(
            cocktailName = cocktail_name + it,
            image = imageCocktail
        )
    }

    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenActions>()
    fun send(event: MainScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        when (event) {
            // l'activity è pronta
            MainScreenEvents.OnReady -> {
                states.postValue(MainScreenStates.Content(cocktailList))
            }
            is MainScreenEvents.OnCocktailClick -> {
                Log.d("COCKTAIL", event.cocktail.toString())
                actions.postValue(MainScreenActions.NavigateToDetail(event.cocktail))
            }
        }
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class MainScreenStates {
    object Loading : MainScreenStates()
    object Error : MainScreenStates()
    data class Content(val coctails: List<CocktailUI>) : MainScreenStates()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class MainScreenEvents {
    data class OnCocktailClick(val cocktail: CocktailUI) : MainScreenEvents()
    object OnReady : MainScreenEvents()
}

sealed class MainScreenActions {
    data class NavigateToDetail(val cocktail: CocktailUI) : MainScreenActions()
}
