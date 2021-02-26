package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        }.exhaustive
    }


    /*private fun onSuccess(result: LoadCocktailResult.Success) {
        val cocktails = result.cocktails.map {
            CocktailUI(cocktailName = it.name, image = it.image, id = it.idDrink)
        }
        states.postValue(SearchScreenStates.Content(cocktails))
    }*/
}

sealed class MainActivityScreenStates {
    object Loading : MainActivityScreenStates()
    data class Error(val error: ErrorStates) : MainActivityScreenStates()
    data class Content(val cocktails: List<SearchCocktailUI>) : MainActivityScreenStates()
}

sealed class MainActivityScreenEvent {
    data class OnSearchClick(val query: String) : MainActivityScreenEvent()
}

sealed class MainActivityScreenAction {
    data class NavigateToSearch(val query: String) : MainActivityScreenAction()
}
