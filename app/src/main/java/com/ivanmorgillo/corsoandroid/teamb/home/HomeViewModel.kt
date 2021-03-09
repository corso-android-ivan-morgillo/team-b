package com.ivanmorgillo.corsoandroid.teamb.home

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.networking.CategoriesError
import com.apperol.networking.LoadCategoriesResult
import com.apperol.networking.LoadCocktailError
import com.apperol.networking.LoadCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoCategoriesFound
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.home.ErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.home.HomeScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
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

    init {
        tracking.logScreen(Screens.Home)
    }

    fun send(event: HomeScreenEvents) {
        // controlla il tipo di evento e in base a questo fa qualcosa
        Timber.d(event.toString())
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (event) {
            // l'activity è pronta
            HomeScreenEvents.OnReady -> {
                loadContent("Ordinary Drink")
            }
            is HomeScreenEvents.OnCocktailClick -> {
                tracking.logEvent("home_cocktail_clicked")
                actions.postValue(HomeScreenActions.NavigateToDetail(event.drinks))
            }
            HomeScreenEvents.OnRefreshClicked -> {
                tracking.logEvent("home_refresh_clicked")
                loadContent("Ordinary Drink")
            }
            HomeScreenEvents.OnSettingClick -> {
                tracking.logEvent("home_settings_clicked")
                actions.postValue(HomeScreenActions.NavigateToSettings)
            }
            is HomeScreenEvents.OnCategoryClick -> {
                val param = Bundle()
                param.putString("category_clicked", event.category.nameCategory)
                tracking.logEvent("home_category_clicked", param)
                loadContent(event.category.nameCategory)
            }
        }.exhaustive
    }

    private fun onCategoriesFailure(result: LoadCategoriesResult.Failure) {
        when (result.error) {
            CategoriesError.NoCategoriesFound -> states.postValue(Error(ShowNoCategoriesFound))
            CategoriesError.NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            CategoriesError.ServerError -> states.postValue(Error(ShowServerError))
            CategoriesError.SlowInternet -> states.postValue(Error(ShowSlowInternet))
        }.exhaustive
    }

    private fun loadContent(category: String) {
        states.postValue(Loading)
        viewModelScope.launch {
            val drinkresult = repository.loadDrinks(category)
            val resultCategories = repository.loadCategories()
            when (resultCategories) {
                is LoadCategoriesResult.Failure -> onCategoriesFailure(resultCategories)
                is LoadCategoriesResult.Success -> {
                    val categories = resultCategories.categories.map {
                        CategoryUI(
                            nameCategory = it.categoryName,
                            imageCategory = "https://www.thecocktaildb.com/images/media/drink/ruxuvp1472669600.jpg"
                        )
                    }
                    when (drinkresult) {
                        is LoadCocktailResult.Failure -> onFailure(drinkresult)
                        is LoadCocktailResult.Success -> {
                            val cocktails = drinkresult.cocktails.map {
                                DrinksUI(drinkName = it.name, image = it.image, id = it.idDrink)
                            }
                            val generalContent = GeneralContent(cocktails, categories)
                            states.postValue(Content(generalContent))
                        }
                    }.exhaustive
                }
            }.exhaustive
        }
    }

    private fun onFailure(result: LoadCocktailResult.Failure) {
        when (result.error) {
            LoadCocktailError.NoCocktailFound -> states.postValue(Error(ShowNoCocktailFound))
            LoadCocktailError.NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            LoadCocktailError.ServerError -> states.postValue(Error(ShowServerError))
            LoadCocktailError.SlowInternet -> states.postValue(Error(ShowSlowInternet))
        }.exhaustive
    }
}

/**
 * Contiene gli oggetti che rappresentano lo stato in cui si può trovare la nostra schermata:
 * Loading, Error, Content
 */
sealed class HomeScreenStates {
    object Loading : HomeScreenStates()
    data class Error(val error: ErrorStates) : HomeScreenStates()
    data class Content(val generalContent: GeneralContent) : HomeScreenStates()
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
}

sealed class ErrorStates {
    object ShowNoCategoriesFound : ErrorStates()
    object ShowNoInternetMessage : ErrorStates()
    object ShowNoCocktailFound : ErrorStates()
    object ShowServerError : ErrorStates()
    object ShowSlowInternet : ErrorStates()
}

class GeneralContent(val drinksList: List<DrinksUI>, val categoryList: List<CategoryUI>)

data class DrinksUI(
    val drinkName: String,
    val image: String,
    val id: Long
)

data class CategoryUI(
    val nameCategory: String,
    val imageCategory: String
)
