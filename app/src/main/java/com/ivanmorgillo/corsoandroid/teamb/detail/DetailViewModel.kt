package com.ivanmorgillo.corsoandroid.teamb.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teamb.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.Tracking
import com.ivanmorgillo.corsoandroid.teamb.exhaustive
import com.ivanmorgillo.corsoandroid.teamb.network.DetailLoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.DetailLoadCocktailError.NoDetailFound
import com.ivanmorgillo.corsoandroid.teamb.network.DetailLoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.DetailLoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.DetailLoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadDetailCocktailResult
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: CocktailRepository,
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<DetailScreenStates>()

    fun send(event: DetailScreenEvents) {
        when (event) {
            is DetailScreenEvents.OnReady -> loadDetails(event.id)
        }
    }

    private fun loadDetails(id: Long) {
        states.postValue(DetailScreenStates.Loading)
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
        val ingredientsUI = details.ingredients.map {
            IngredientUI(nomeIngr = it.name, ingrQty = it.quantity)
        }
        val content: List<DetailScreenItems> = listOf(
            //DetailScreenItems.Title(details.name),
            DetailScreenItems.Image(details.image, details.name),
            DetailScreenItems.Video(details.youtubeLink),
            DetailScreenItems.GlassType(details.glass, details.isAlcoholic),
            DetailScreenItems.IngredientList(ingredientsUI),
            DetailScreenItems.Instructions(details.instructions),

            )
        states.postValue(DetailScreenStates.Content(content))
    }

    private fun onFailure(result: LoadDetailCocktailResult.Failure) {
        when (result.error) {
            NoCocktailFound -> states.postValue(DetailScreenStates.Error(DetailErrorStates.ShowNoCocktailFound))
            NoInternet -> states.postValue(DetailScreenStates.Error(DetailErrorStates.ShowNoInternetMessage))
            ServerError -> states.postValue(DetailScreenStates.Error(DetailErrorStates.ShowServerError))
            SlowInternet -> states.postValue(DetailScreenStates.Error(DetailErrorStates.ShowSlowInternet))
            NoDetailFound -> states.postValue(DetailScreenStates.Error(DetailErrorStates.ShowNoDetailFound))
        }.exhaustive
    }
}

sealed class DetailScreenStates {
    object Loading : DetailScreenStates()
    data class Error(val error: DetailErrorStates) : DetailScreenStates()
    data class Content(val details: List<DetailScreenItems>) : DetailScreenStates()
}

sealed class DetailScreenEvents {
    data class OnReady(val id: Long) : DetailScreenEvents()
}


sealed class DetailErrorStates {
    object ShowNoInternetMessage : DetailErrorStates()
    object ShowNoCocktailFound : DetailErrorStates()
    object ShowServerError : DetailErrorStates()
    object ShowSlowInternet : DetailErrorStates()
    object ShowNoDetailFound : DetailErrorStates()
}
