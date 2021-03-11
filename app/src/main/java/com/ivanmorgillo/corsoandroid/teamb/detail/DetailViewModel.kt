package com.ivanmorgillo.corsoandroid.teamb.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.networking.DetailLoadCocktailError.NoCocktailFound
import com.apperol.networking.DetailLoadCocktailError.NoDetailFound
import com.apperol.networking.DetailLoadCocktailError.NoInternet
import com.apperol.networking.DetailLoadCocktailError.ServerError
import com.apperol.networking.DetailLoadCocktailError.SlowInternet
import com.apperol.networking.LoadDetailCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoDetailFound
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowServerError
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates.ShowSlowInternet
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.LoadDrink
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenEvents.LoadRandomDrink
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: CocktailRepository
) : ViewModel() {
    val states = MutableLiveData<DetailScreenStates>()
    fun send(event: DetailScreenEvents) {
        when (event) {
            is LoadDrink -> loadDetails(event.id)
            is LoadRandomDrink -> loadRandomDrink()
        }.exhaustive
    }

    private fun loadRandomDrink() {
        viewModelScope.launch {
            val result = repository.loadRandomDetailCocktails()
            when (result) {
                is LoadDetailCocktailResult.Failure -> onFailure(result)
                is LoadDetailCocktailResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun loadDetails(id: Long) {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadDetailCocktails(cocktailId = id)
            when (result) {
                is LoadDetailCocktailResult.Failure -> onFailure(result)
                is LoadDetailCocktailResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadDetailCocktailResult.Success) {
        val details = result.details
        val ingredientsUI = details.ingredients
            .filter { it.name.isNotBlank() && it.quantity.isNotBlank() }
            .map {
                IngredientUI(nomeIngr = it.name, ingrQty = it.quantity)
            }
        val content: List<DetailScreenItems> = listOf(
            DetailScreenItems.Image(details.image, details.name),
            DetailScreenItems.Video(details.youtubeLink),
            DetailScreenItems.GlassType(details.glass, details.isAlcoholic),
            DetailScreenItems.IngredientList(ingredientsUI),
            DetailScreenItems.Instructions(details.instructions),
        )
        states.postValue(Content(content))
    }

    private fun onFailure(result: LoadDetailCocktailResult.Failure) {
        when (result.error) {
            NoCocktailFound -> states.postValue(DetailScreenStates.Error(ShowNoCocktailFound))
            NoInternet -> states.postValue(DetailScreenStates.Error(ShowNoInternetMessage))
            ServerError -> states.postValue(DetailScreenStates.Error(ShowServerError))
            SlowInternet -> states.postValue(DetailScreenStates.Error(ShowSlowInternet))
            NoDetailFound -> states.postValue(DetailScreenStates.Error(ShowNoDetailFound))
        }.exhaustive
    }
}

sealed class DetailScreenStates {
    object Loading : DetailScreenStates()
    data class Error(val error: DetailErrorStates) : DetailScreenStates()
    data class Content(val details: List<DetailScreenItems>) : DetailScreenStates()
}

sealed class DetailScreenEvents {
    object LoadRandomDrink : DetailScreenEvents()
    data class LoadDrink(val id: Long) : DetailScreenEvents()
}

sealed class DetailErrorStates {
    object ShowNoInternetMessage : DetailErrorStates()
    object ShowNoCocktailFound : DetailErrorStates()
    object ShowServerError : DetailErrorStates()
    object ShowSlowInternet : DetailErrorStates()
    object ShowNoDetailFound : DetailErrorStates()
}
