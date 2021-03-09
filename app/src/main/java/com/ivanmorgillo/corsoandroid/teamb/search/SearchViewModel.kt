package com.ivanmorgillo.corsoandroid.teamb.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.networking.LoadSearchCocktailResult
import com.apperol.networking.SearchLoadCocktailError.NoCocktailFound
import com.apperol.networking.SearchLoadCocktailError.NoInternet
import com.apperol.networking.SearchLoadCocktailError.ServerError
import com.apperol.networking.SearchLoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.search.SearchErrorStates.ShowNoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.search.SearchErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.search.SearchErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.search.SearchErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.search.SearchScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.search.SearchScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.search.SearchScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
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

    init {
        tracking.logScreen(Screens.Search)
    }

    fun send(event: SearchScreenEvent) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            is SearchScreenEvent.OnCocktailClick -> {
                tracking.logEvent("search_clicked")
                actions.postValue(SearchScreenAction.NavigateToDetail(event.cocktail))
            }
            is SearchScreenEvent.OnReady -> loadContent(event.query)
        }.exhaustive
    }

    private fun loadContent(query: String) {
        states.postValue(Loading)
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
                alcoholic = it.alcoholic
            )
        }
        states.postValue(Content(cocktails))
    }

    private fun onFailure(result: LoadSearchCocktailResult.Failure) {
        Timber.d("Failure")
        when (result.error) {
            NoCocktailFound -> states.postValue(Error(ShowNoCocktailFound))
            NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            ServerError -> states.postValue(Error(ShowServerError))
            SlowInternet -> states.postValue(Error(ShowSlowInternet))
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
    val alcoholic: String
)

sealed class SearchErrorStates {
    object ShowNoInternetMessage : SearchErrorStates()
    object ShowNoCocktailFound : SearchErrorStates()
    object ShowServerError : SearchErrorStates()
    object ShowSlowInternet : SearchErrorStates()
}
