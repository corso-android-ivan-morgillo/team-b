package com.ivanmorgillo.corsoandroid.teamb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import com.ivanmorgillo.corsoandroid.teamb.utils.exhaustive
import kotlinx.coroutines.launch

class CustomListViewModel(
    val tracking: Tracking,
    val repository: CustomDrinkRepository
) : ViewModel() {

    private var customList: List<CustomDrinkUI>? = null
    val states = MutableLiveData<CustomScreenStates>()
    val actions = SingleLiveEvent<CustomScreenAction>()

    init {
        tracking.logScreen(Screens.Custom)
    }

    fun send(event: CustomScreenEvent) {
        when (event) {
            is CustomScreenEvent.OnCustomClick -> {
                tracking.logEvent("favorite_clicked")
                actions.postValue(CustomScreenAction.NavigateToDetail(event.cocktail))
            }
            is CustomScreenEvent.OnDeleteClick -> {
                onDeleteClick(event.cocktail) //
            }
            CustomScreenEvent.OnReady -> loadContent()
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(CustomScreenStates.Loading)
        viewModelScope.launch {
            val customs = repository.loadAll()
            if (customs == null) {
                states.postValue(CustomScreenStates.Error(CustomErrorStates.CustomListEmpty))
            } else {
                customList = customs.map {
                    CustomDrinkUI(
                        drinkName = it.name,
                        drinkImage = it.image,
                        drinkId = it.id,
                        drinkType = it.category
                    )
                }
                val content = CustomScreenStates.Content(customList!!)
                states.postValue(content)
            }
        }
    }

    private fun onDeleteClick(cocktail: CustomDrinkUI) {
        tracking.logEvent("custom_drink_delete")
        viewModelScope.launch {
            repository.delete(cocktail.drinkId)
            loadContent()
        }
    }
}

sealed class CustomScreenEvent {
    data class OnCustomClick(val cocktail: CustomDrinkUI) : CustomScreenEvent()
    data class OnDeleteClick(val cocktail: CustomDrinkUI) : CustomScreenEvent()
    object OnReady : CustomScreenEvent()
}

sealed class CustomScreenAction {
    data class NavigateToDetail(val cocktail: CustomDrinkUI) : CustomScreenAction()
}

sealed class CustomScreenStates {
    object Loading : CustomScreenStates()
    data class Error(val error: CustomErrorStates) : CustomScreenStates()
    data class Content(val customDrink: List<CustomDrinkUI>) : CustomScreenStates()
}

sealed class CustomErrorStates {
    object ShowNoInternetMessage : CustomErrorStates()
    object ShowServerError : CustomErrorStates()
    object ShowSlowInternet : CustomErrorStates()
    object CustomListEmpty : CustomErrorStates()
}
