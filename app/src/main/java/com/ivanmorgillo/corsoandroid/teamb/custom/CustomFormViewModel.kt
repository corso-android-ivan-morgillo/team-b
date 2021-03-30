package com.ivanmorgillo.corsoandroid.teamb.custom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.apperol.Detail
import com.apperol.Ingredient
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormAction.NavigateToCustoms
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.AddIngredient
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.IsAlcoholicClicked
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.OnGlassClicked
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormEvents.OnSaveClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormStates.Content
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormStates.Loading
import com.ivanmorgillo.corsoandroid.teamb.custom.NameField.Invalid
import com.ivanmorgillo.corsoandroid.teamb.custom.NameField.Valid
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch

class CustomFormViewModel(
    val customDrinkRepository: CustomDrinkRepository,
    val tracking: Tracking,
) : ViewModel() {
    val states = MutableLiveData<CustomFormStates>()
    val action = SingleLiveEvent<CustomFormAction>()
    private var content = CustomFormUI(
        name = Valid(""),
        type = "",
        isAlcoholic = true,
        glass = "",
        ingredients = listOf(),
        ingredientName = "",
        ingredientQty = "",
        ingredientUM = "",
        instructions = ""
    )

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: CustomFormEvents) {
        when (event) {
            is OnSaveClick -> viewModelScope.launch { saveContent(event) }
            is OnGlassClicked -> {
                val updatedContent = content.copy(
                    glass = event.glass
                )
                content = updatedContent
                states.postValue(Content(content))
            }
            is IsAlcoholicClicked -> TODO()
            is AddIngredient -> {
                val ingredients = content.ingredients
                    .plus("${event.ingredientName} -- ${event.ingredientQty} ${event.ingredientUM}")
                    .distinct()
                val updatedContent = content.copy(
                    name = Valid(event.name),
                    type = event.type,
                    isAlcoholic = event.isAlcoholic,
                    glass = "",
                    ingredients = ingredients,
                    ingredientName = "",
                    ingredientQty = "",
                    ingredientUM = "",
                    instructions = event.instructions,
                )
                content = updatedContent
                states.postValue(Content(content))
            }
            OnReady -> {

                states.postValue(Content(content))
            }
        }.exhaustive
    }

    private suspend fun saveContent(event: OnSaveClick) {
        val drinkName = event.name
        if (drinkName.isBlank()) {
            val nameErrorContent = content.copy(name = Invalid("Drink name cannot be empty"))
            states.postValue(Content(nameErrorContent))
            return
        }
        val drinkType = event.type
        val drinkAlcoholic = event.alcoholic
        val drinkGlass = event.glass
        // val ingredientName = event.ingredientName
        // val ingredientQty = "${event.ingredientQty} ${event.ingredientUM}"
        val instructions = event.instructions

        states.postValue(Loading)
        println("Content ingredients: ${content.ingredients}")
        val ingredients = content.ingredients
            // .plus("$ingredientName $ingredientQty")
            .map {
                val list = it.split(" -- ")
                val ingredientName = list.first()
                val ingredientQty = list.last()
                println("Quantità: $ingredientQty")
                Ingredient(name = ingredientName, quantity = ingredientQty)
            }
            .filter { it.name.isNotBlank() && it.quantity.isNotBlank() }
        println("Ingredienti: $ingredients")
        val dettaglio = Detail(
            name = drinkName,
            image = "",
            id = 0,
            isAlcoholic = drinkAlcoholic,
            glass = drinkGlass,
            ingredients = ingredients,
            youtubeLink = null,
            instructions = instructions,
            category = drinkType
        )
        customDrinkRepository.save(dettaglio)
        action.postValue(NavigateToCustoms)
    }
}

sealed class CustomFormAction {
    object NavigateToCustoms : CustomFormAction()
}

sealed class CustomFormStates {
    object Loading : CustomFormStates()
    data class Error(val error: DetailErrorStates) : CustomFormStates()
    data class Content(val content: CustomFormUI) : CustomFormStates()
}

sealed class CustomFormEvents {
    data class IsAlcoholicClicked(val checked: Boolean) : CustomFormEvents()
    data class OnGlassClicked(val glass: String) : CustomFormEvents()
    data class AddIngredient(
        val name: String,
        val type: String,
        val isAlcoholic: Boolean,
        val glass: String,
        val ingredientName: String,
        val ingredientQty: String,
        val ingredientUM: String,
        val instructions: String,
    ) : CustomFormEvents()

    data class OnSaveClick(
        val name: String,
        val type: String,
        val alcoholic: Boolean,
        val glass: String,
        val ingredientName: String,
        val ingredientQty: String,
        val ingredientUM: String,
        val instructions: String,
    ) : CustomFormEvents()

    object OnReady : CustomFormEvents()
}
