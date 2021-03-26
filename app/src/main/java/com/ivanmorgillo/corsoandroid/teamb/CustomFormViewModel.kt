package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.apperol.Detail
import com.apperol.Ingredient
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.AddIngredient
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.IsAlcoholicClicked
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnGlassClicked
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnSaveClick
import com.ivanmorgillo.corsoandroid.teamb.CustomFormStates.Content
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch

class CustomFormViewModel(
    val customDrinkRepository: CustomDrinkRepository,
    val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<CustomFormStates>()
    private val ingredientsList = emptyList<String>().toMutableList()
    private var content = CustomFormUI(
        name = "",
        type = "",
        isAlcoholic = true,
        glass = "",
        ingredients = listOf(),
        ingredientName = "",
        ingredientQty = "",
        instructions = ""
    )

    fun send(event: CustomFormEvents) {
        when (event) {
            is OnSaveClick -> viewModelScope.launch { saveContent(event) }
            OnGlassClicked -> TODO()
            is IsAlcoholicClicked -> TODO()
            is AddIngredient -> {
                val ingredients = content.ingredients
                    .plus("${event.ingredientName} -- ${event.ingredientQty}")
                    .distinct()
                val updatedContent = content.copy(
                    name = event.name,
                    type = event.type,
                    isAlcoholic = event.isAlcoholic,
                    glass = event.glass,
                    ingredients = ingredients,
                    ingredientName = "",
                    ingredientQty = "",
                    instructions = event.instructions
                )
                content = updatedContent
                states.postValue(Content(content))

            }
            OnReady -> {

                states.postValue(CustomFormStates.Content(content))
            }
        }.exhaustive
    }

    private suspend fun saveContent(event: OnSaveClick) {
        states.postValue(CustomFormStates.Loading)
        val ingredients = content.ingredients.plus("${event.ingredientName} ${event.ingredientQty}").map {
            val list = it.split(" -- ")
            val ingredientName = list.first()
            val ingredientQty = list.last()
            Ingredient(name = ingredientName, quantity = ingredientQty)
        }
        val dettaglio = Detail(
            name = event.name,
            image = "",
            id = 0,
            isAlcoholic = event.alcoholic,
            glass = "",
            ingredients = ingredients,
            youtubeLink = null,
            instructions = event.instructions,
            category = ""
        )
        customDrinkRepository.save(dettaglio)
    }
}

sealed class CustomFormStates {
    object Loading : CustomFormStates()
    data class Error(val error: DetailErrorStates) : CustomFormStates()
    data class Content(val content: CustomFormUI) : CustomFormStates()
}

sealed class CustomFormEvents {
    data class IsAlcoholicClicked(val checked: Boolean) : CustomFormEvents()
    object OnGlassClicked : CustomFormEvents()
    data class AddIngredient(
        val name: String,
        val type: String,
        val isAlcoholic: Boolean,
        val glass: String,
        val ingredientName: String,
        val ingredientQty: String,
        val instructions: String
    ) : CustomFormEvents()

    data class OnSaveClick(
        val name: String,
        val type: String,
        val alcoholic: Boolean,
        val glass: String,
        val ingredientName: String,
        val ingredientQty: String,
        val instructions: String
    ) : CustomFormEvents()

    object OnReady : CustomFormEvents()
}
