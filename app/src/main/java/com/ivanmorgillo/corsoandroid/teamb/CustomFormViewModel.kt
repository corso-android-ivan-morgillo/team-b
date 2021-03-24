package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.apperol.Detail
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.AddIngredient
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.IsAlcoholicClicked
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnGlassClicked
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnReady
import com.ivanmorgillo.corsoandroid.teamb.CustomFormEvents.OnSaveClick
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailErrorStates
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class CustomFormViewModel(
    val customDrinkRepository: CustomDrinkRepository,
    val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<CustomFormStates>()

    private var drinkCustom: Detail? = null

    fun send(event: CustomFormEvents) {
        when (event) {
            OnSaveClick -> viewModelScope.launch { saveContent() }
            OnGlassClicked -> TODO()
            is IsAlcoholicClicked -> TODO()
            AddIngredient -> TODO()
            OnReady -> {
                val content = CustomFormUI(
                    name = "PippoDrink",
                    type = "Ordinary Drink",
                    isAlcoholic = true,
                    glass = "flute",
                    ingredients = listOf("sugar", "vodka", "gin"),
                    ingredientName = "",
                    ingredientQty = "",
                    instructions = "mirko la fa poi"
                )
                states.postValue(CustomFormStates.Success(content))
            }
        }.exhaustive
    }

    private suspend fun saveContent() {
        states.postValue(CustomFormStates.Loading)
        val custom = drinkCustom ?: return
        customDrinkRepository.save(custom)
        Timber.d("Inserito Drink Custom")
    }
}

sealed class CustomFormStates {
    object Loading : CustomFormStates()
    data class Error(val error: DetailErrorStates) : CustomFormStates()
    data class Success(val content: CustomFormUI) : CustomFormStates()
}

sealed class CustomFormEvents {
    class IsAlcoholicClicked(checked: Boolean) : CustomFormEvents()
    object OnSaveClick : CustomFormEvents()
    object OnGlassClicked : CustomFormEvents()
    object AddIngredient : CustomFormEvents()
    object OnReady : CustomFormEvents()
}
