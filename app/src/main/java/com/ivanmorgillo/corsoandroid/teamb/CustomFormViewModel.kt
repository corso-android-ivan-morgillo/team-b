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
import com.ivanmorgillo.corsoandroid.teamb.NameField.Invalid
import com.ivanmorgillo.corsoandroid.teamb.NameField.Valid
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class CustomFormViewModel(
    val customDrinkRepository: CustomDrinkRepository,
    val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<CustomFormStates>()
    val action = SingleLiveEvent<CustomFormAction>()
    private val ingredientsList = emptyList<String>().toMutableList()
    private var content = CustomFormUI(
        name = Valid(""),
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
                    name = Valid(event.name),
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
        val drinkName = event.name
        if (drinkName.isBlank()) {
            val nameErrorContent = content.copy(name = Invalid("Drink name cannot be empty"))
            states.postValue(CustomFormStates.Content(nameErrorContent))
            return
        }
        val drinkType = event.type
        val drinkAlcoholic = event.alcoholic
        val drinkGlass = event.glass
        val ingredientName = event.ingredientName
        val ingredientQty = event.ingredientQty
        val instructions = event.instructions
        Timber.d("EVENTO: $event")

        states.postValue(CustomFormStates.Loading)

        val ingredients = content.ingredients
            .plus("$ingredientName $ingredientQty")
            .map {
                val list = it.split(" -- ")
                val ingredientName = list.first()
                val ingredientQty = list.last()
                Ingredient(name = ingredientName, quantity = ingredientQty)
            }
            .filter { it.name.isNotBlank() && it.quantity.isNotBlank() }
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
        Timber.d("SALVA IL DETTAGLIO $dettaglio")
        customDrinkRepository.save(dettaglio)
        action.postValue(CustomFormAction.NavigateToCustoms)
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
