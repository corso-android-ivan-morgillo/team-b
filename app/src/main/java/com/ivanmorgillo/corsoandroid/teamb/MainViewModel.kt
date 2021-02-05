package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val MAXRANGE = 10

class MainViewModel : ViewModel() {
    private val imageCocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
    private val cocktailList = (1..MAXRANGE).map {
        CocktailUI(
            cocktailName = "Mojito",
            image = imageCocktail
        )
    }

    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<MainScreenState>()

    fun send(event: MainScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        when (event) {
            // l'activity è pronta
            MainScreenEvents.OnReady -> {
                states.postValue(MainScreenState.Content(cocktailList))
            }
        }
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class MainScreenState {
    object Loading : MainScreenState()
    object Error : MainScreenState()
    data class Content(val coctails: List<CocktailUI>) : MainScreenState()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class MainScreenEvents {
    object OnReady : MainScreenEvents()
}
