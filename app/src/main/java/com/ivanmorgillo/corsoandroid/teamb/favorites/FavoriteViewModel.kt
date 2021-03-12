package com.ivanmorgillo.corsoandroid.teamb.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.Favorite
import com.apperol.FavoriteRepository
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenEvent.OnCocktailClick
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenEvent.OnDeleteClick
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteViewModel(
    private val repository: FavoriteRepository,
    private val tracking: Tracking

) : ViewModel() {
    private var cocktailList: List<Favorite>? = null
    val states = MutableLiveData<FavoriteScreenStates>()
    val actions = SingleLiveEvent<FavoriteScreenAction>()

    init {
        tracking.logScreen(Screens.Favorite)
    }

    fun send(event: FavoriteScreenEvent) {
        when (event) {
            is OnCocktailClick -> {
                tracking.logEvent("favorite_clicked")
                actions.postValue(NavigateToDetail(event.cocktail))
            }
            OnReady -> loadContent()
            is OnDeleteClick -> onDeleteClick(event.cocktail)
        }.exhaustive
    }

    private fun onDeleteClick(cocktail: FavoriteCocktailUI) {
        tracking.logEvent("favorite_cocktail_delete")
        viewModelScope.launch {
            repository.delete(cocktail.id)
            loadContent()
        }
    }

    private fun loadContent() {
        states.postValue(Loading)
        viewModelScope.launch {
            val cocktails = repository.loadAll()
            Timber.d("cocktails = $cocktails")
            if (cocktails.isEmpty()) {
                states.postValue(FavoriteScreenStates.Error(FavoriteErrorStates.FavoriteListEmpty))
            } else {
                cocktailList = cocktails
                val favoriteUI = cocktails.map {
                    FavoriteCocktailUI(
                        cocktailName = it.name,
                        image = it.image,
                        id = it.id,
                        category = it.category

                    )
                }
                val content = FavoriteScreenStates.Content(favoriteUI)
                states.postValue(content)
            }
        }
    }
}

sealed class FavoriteScreenEvent {
    data class OnCocktailClick(val cocktail: FavoriteCocktailUI) : FavoriteScreenEvent()
    data class OnDeleteClick(val cocktail: FavoriteCocktailUI) : FavoriteScreenEvent()
    object OnReady : FavoriteScreenEvent()
}

sealed class FavoriteScreenAction {
    data class NavigateToDetail(val cocktail: FavoriteCocktailUI) : FavoriteScreenAction()
}

sealed class FavoriteScreenStates {
    object Loading : FavoriteScreenStates()
    data class Error(val error: FavoriteErrorStates) : FavoriteScreenStates()
    data class Content(val favorites: List<FavoriteCocktailUI>) : FavoriteScreenStates()
}

sealed class FavoriteErrorStates {
    object ShowNoInternetMessage : FavoriteErrorStates()
    object ShowServerError : FavoriteErrorStates()
    object ShowSlowInternet : FavoriteErrorStates()
    object FavoriteListEmpty : FavoriteErrorStates()
}
