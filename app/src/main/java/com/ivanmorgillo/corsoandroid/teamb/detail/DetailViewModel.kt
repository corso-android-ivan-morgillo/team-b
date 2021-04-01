package com.ivanmorgillo.corsoandroid.teamb.detail

import android.content.DialogInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.AuthenticationManager
import com.apperol.CocktailRepository
import com.apperol.Detail
import com.apperol.DetailLoadCocktailError.NoDetailFound
import com.apperol.DetailLoadCocktailError.NoInternet
import com.apperol.DetailLoadCocktailError.ServerError
import com.apperol.DetailLoadCocktailError.SlowInternet
import com.apperol.FavoriteRepository
import com.apperol.LoadDetailCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoDetailFound
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoLoggedUserError
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.CancelClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.NavigateToSetting
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenActions.SignIn
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.LoadDrink
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.LoadRandomDrink
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.OnCancelClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.OnFavoriteClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.OnSettingClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.OnSignInClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel(
    private val repository: CocktailRepository,
    private val favoriteRepository: FavoriteRepository,
    private val authenticationManager: AuthenticationManager,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<DetailScreenStates>()
    val actions = SingleLiveEvent<DetailScreenActions>()
    private var cocktailDetail: Detail? = null
    private var isFavorite: Boolean = false

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: DetailScreenEvents) {
        when (event) {
            is LoadDrink -> loadDetails(event.id, event.isCustom)
            is LoadRandomDrink -> loadRandomDrink()
            OnFavoriteClick -> {
                viewModelScope.launch {
                    if (authenticationManager.isUserLoggedIn()) {
                        if (isFavorite) {
                            removeFavorite()
                        } else {
                            saveFavorite()
                        }
                    } else {
                        states.postValue(Error(ShowNoLoggedUserError))
                    }
                }
            }
            OnSettingClick -> {
                tracking.logEvent("no_internet_on_setting_click")
                actions.postValue(NavigateToSetting)
            }
            OnSignInClick -> {
                tracking.logEvent("sign_in_on_dialog_click")
                actions.postValue(SignIn)
            }
            is OnCancelClick -> {
                tracking.logEvent("cancel_on_dialog_click")
                actions.postValue(CancelClick(event.dialog))
            }
        }.exhaustive
    }

    private suspend fun saveFavorite() {
        val cocktail = cocktailDetail ?: return
        val updateFavorite = !isFavorite
        favoriteRepository.save(cocktail)
        this.isFavorite = updateFavorite
        Timber.d(" inserito FAVORITO")
        createContent(cocktail)
    }

    private suspend fun removeFavorite() {
        val cocktail = cocktailDetail ?: return
        val updateFavorite = !isFavorite
        favoriteRepository.delete(cocktail.id)
        this.isFavorite = updateFavorite
        Timber.d("Eliminato FAVORITO")
        createContent(cocktail)
    }

    private fun loadRandomDrink() {
        viewModelScope.launch {
            val result = repository.loadRandomDetailCocktails()
            when (result) {
                is LoadDetailCocktailResult.Failure -> onFailure(result)
                is LoadDetailCocktailResult.Success -> createContent(result.details)
            }.exhaustive
        }
    }

    private fun loadDetails(id: Long, isCustom: Boolean) {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadDetailCocktails(cocktailId = id, isCustom)
            when (result) {
                is LoadDetailCocktailResult.Failure -> onFailure(result)
                is LoadDetailCocktailResult.Success -> createContent(result.details)
            }.exhaustive
        }
    }

    private suspend fun createContent(details: Detail) {
        this.cocktailDetail = details
        isFavorite = favoriteRepository.isFavorite(details.id)

        // this.isFavorite = isFavorite
        val ingredientsUI = details.ingredients
            .filter { it.name.isNotBlank() && it.quantity.isNotBlank() }
            .map {
                IngredientUI(nomeIngr = it.name, ingrQty = it.quantity)
            }
        val content: List<DetailScreenItems> = listOf(
            DetailScreenItems.Image(details.image, details.name, isFavorite),
            DetailScreenItems.Video(details.youtubeLink),
            DetailScreenItems.GlassType(details.glass, details.isAlcoholic),
            DetailScreenItems.IngredientList(ingredientsUI),
            DetailScreenItems.Instructions(details.instructions),
        )
        states.postValue(Content(content))
    }

    private fun onFailure(result: LoadDetailCocktailResult.Failure) {
        when (result.error) {
            NoInternet -> states.postValue(Error(ShowNoInternetMessage))
            ServerError -> states.postValue(Error(ShowServerError))
            SlowInternet -> states.postValue(Error(ShowSlowInternet))
            NoDetailFound -> states.postValue(Error(ShowNoDetailFound))
        }.exhaustive
    }
}

sealed class DetailScreenActions {
    object NavigateToSetting : DetailScreenActions()
    object SignIn : DetailScreenActions()
    data class CancelClick(val dialog: DialogInterface) : DetailScreenActions()
}

sealed class DetailScreenStates {
    object Loading : DetailScreenStates()
    data class Error(val error: DetailErrorStates) : DetailScreenStates()
    data class Content(val details: List<DetailScreenItems>) : DetailScreenStates()
}

sealed class DetailScreenEvents {
    object LoadRandomDrink : DetailScreenEvents()
    object OnSettingClick : DetailScreenEvents()
    data class LoadDrink(val id: Long, val isCustom: Boolean) : DetailScreenEvents()
    object OnFavoriteClick : DetailScreenEvents()
    object OnSignInClick : DetailScreenEvents()
    data class OnCancelClick(val dialog: DialogInterface) : DetailScreenEvents()
}

sealed class DetailErrorStates {
    object ShowNoInternetMessage : DetailErrorStates()
    object ShowServerError : DetailErrorStates()
    object ShowSlowInternet : DetailErrorStates()
    object ShowNoDetailFound : DetailErrorStates()
    object ShowNoLoggedUserError : DetailErrorStates()
}
