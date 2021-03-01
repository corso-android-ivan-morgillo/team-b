package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.network.LoadSearchCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.network.SearchLoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.SearchLoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.SearchLoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.SearchLoadCocktailError.SlowInternet
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking

) : ViewModel() {
    // mutable live data: tipo contenitore di T, dove T è il nostro stato
    // states è una variabile che la nostra activity può osservare.
    // Quando si cambia stato questa variabile viene settata
    val states = MutableLiveData<SearchScreenStates>()
    val actions = SingleLiveEvent<SearchScreenAction>()

    fun send(event: SearchScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is SearchScreenEvent.OnCocktailClick -> {
                actions.postValue(SearchScreenAction.NavigateToDetail(event.cocktail))
            }
            is SearchScreenEvent.OnReady -> loadContent(event.query)
        }.exhaustive
    }

    private fun loadContent(query: String) {
        states.postValue(SearchScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadSearchCocktails(query)
            when (result) {
                is LoadSearchCocktailResult.Failure -> onFailure(result)
                is LoadSearchCocktailResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadSearchCocktailResult.Success) {
        val cocktails = result.details.map {
            SearchCocktailUI(
                cocktailName = it.name,
                image = it.image,
                id = it.idDrink,
                category = it.category,
                alcoholic = false
            )
        }
        states.postValue(SearchScreenStates.Content(cocktails))
    }

    private fun onFailure(result: LoadSearchCocktailResult.Failure) {
        Timber.d("Failure")
        when (result.error) {
            NoCocktailFound -> states.postValue(SearchScreenStates.Error(SearchErrorStates.ShowNoCocktailFound))
            NoInternet -> states.postValue(SearchScreenStates.Error(SearchErrorStates.ShowNoInternetMessage))
            ServerError -> states.postValue(SearchScreenStates.Error(SearchErrorStates.ShowServerError))
            SlowInternet -> states.postValue(SearchScreenStates.Error(SearchErrorStates.ShowSlowInternet))
        }.exhaustive
    }
}

sealed class SearchScreenStates {
    object Loading : SearchScreenStates()
    data class Error(val error: SearchErrorStates) : SearchScreenStates()
    data class Content(val cocktails: List<SearchCocktailUI>) : SearchScreenStates()
}

sealed class SearchScreenEvent {
    data class OnCocktailClick(val cocktail: SearchCocktailUI) : SearchScreenEvent()
    data class OnReady(val query: String) : SearchScreenEvent()
}

sealed class SearchScreenAction {
    data class NavigateToDetail(val cocktail: SearchCocktailUI) : SearchScreenAction()
}

data class SearchCocktailUI(
    val cocktailName: String,
    val image: String,
    val id: Long,
    val category: String,
    val alcoholic: Boolean
)

sealed class SearchErrorStates {
    object ShowNoInternetMessage : SearchErrorStates()
    object ShowNoCocktailFound : SearchErrorStates()
    object ShowServerError : SearchErrorStates()
    object ShowSlowInternet : SearchErrorStates()
}
