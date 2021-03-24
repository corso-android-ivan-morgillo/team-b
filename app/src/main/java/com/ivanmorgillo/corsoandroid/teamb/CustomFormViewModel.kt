package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.apperol.Detail
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
        }.exhaustive
    }

    private suspend fun saveContent(): Unit {
        states.postValue(CustomFormStates.Loading)
        val custom = drinkCustom ?: return
        customDrinkRepository.save(custom)
        Timber.d("Inserito Drink Custom")
    }
}

sealed class CustomFormStates {
    object Loading : CustomFormStates()
    data class Error(val error: DetailErrorStates) : CustomFormStates()
    object Success : CustomFormStates()
}

sealed class CustomFormEvents {
    object OnSaveClick : CustomFormEvents()
}
