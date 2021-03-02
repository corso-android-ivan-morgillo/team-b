package com.ivanmorgillo.corsoandroid.teamb.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.DrinksUI
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoCategoriesFound
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.CategoriesContent
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.network.CategoriesError
import com.ivanmorgillo.corsoandroid.teamb.network.CategoriesError.NoCategoriesFound
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCategoriesResult
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
                loadCategoryContent()
                loadContent()
            }
            is HomeScreenEvents.OnCocktailClick -> {
                tracking.logEvent("Cocktail_Clicked")
                actions.postValue(HomeScreenActions.NavigateToDetail(event.drinks))
            }
            HomeScreenEvents.OnRefreshClicked -> {
                // add tracking
                loadContent()
            }
            HomeScreenEvents.OnSettingClick -> {
                actions.postValue(HomeScreenActions.NavigateToSettings)
            }
            is HomeScreenEvents.OnCategoryClick -> {
                actions.postValue(HomeScreenActions.SetDrinkList)
            }
        }.exhaustive
    }

    private fun loadCategoryContent() {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadCategories()
            when (result) {
                is LoadCategoriesResult.Failure -> onCategoriesFailure(result)
                is LoadCategoriesResult.Success -> onCategoriesSuccess(result)
            }.exhaustive
        }
    }

    private fun onCategoriesSuccess(result: LoadCategoriesResult.Success) {
        val categories = result.categories.map {
            CategoryUI(
                nameCategory = it.categoryName,
                imageCategory = "https://www.thecocktaildb.com/images/media/drink/ruxuvp1472669600.jpg"
            )
        }
        states.postValue(CategoriesContent(categories))
    }

    private fun onCategoriesFailure(result: LoadCategoriesResult.Failure) {
        when (result.error) {
            NoCategoriesFound -> states.postValue(Error(ShowNoCategoriesFound))
            CategoriesError.NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            CategoriesError.ServerError -> states.postValue(Error(ShowServerError))
            CategoriesError.SlowInternet -> states.postValue(Error(ShowSlowInternet))
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(Loading)
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
            NoCocktailFound -> states.postValue(Error(ShowNoCocktailFound))
            NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            ServerError -> states.postValue(Error(ShowServerError))
            SlowInternet -> states.postValue(Error(ShowSlowInternet))
        }.exhaustive
    }

    private fun onSuccess(result: Success) {
        val cocktails = result.cocktails.map {
            DrinksUI(drinkName = it.name, image = it.image, id = it.idDrink)
        }
        states.postValue(Content(cocktails))
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    data class Error(val error: ErrorStates) : HomeScreenStates()
    data class Content(val drinks: List<DrinksUI>) : HomeScreenStates()
    data class CategoriesContent(val categories: List<CategoryUI>) : HomeScreenStates()
}

// contiene eventi che possiamo mandare al nostro view model
sealed class HomeScreenEvents {
    data class OnCocktailClick(val drinks: DrinksUI) : HomeScreenEvents()
    data class OnCategoryClick(val category: CategoryUI) : HomeScreenEvents()
    object OnReady : HomeScreenEvents()
    object OnRefreshClicked : HomeScreenEvents()
    object OnSettingClick : HomeScreenEvents()
}

sealed class HomeScreenActions {
    data class NavigateToDetail(val drinks: DrinksUI) : HomeScreenActions()
    object NavigateToSettings : HomeScreenActions()
    object SetDrinkList : HomeScreenActions()
}

sealed class ErrorStates {
    object ShowNoCategoriesFound : ErrorStates()
    object ShowNoInternetMessage : ErrorStates()
    object ShowNoCocktailFound : ErrorStates()
    object ShowServerError : ErrorStates()
    object ShowSlowInternet : ErrorStates()
}
