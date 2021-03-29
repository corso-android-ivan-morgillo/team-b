package com.ivanmorgillo.corsoandroid.teamb.custom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apperol.CustomDrinkRepository
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomErrorStates.CustomListEmpty
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnCustomClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnDeleteClick
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenEvent.OnReady
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Content
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Error
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomScreenStates.Loading
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
            is OnCustomClick -> {
                tracking.logEvent("favorite_clicked")
                actions.postValue(NavigateToDetail(event.cocktail))
            }
            is OnDeleteClick -> {
                onDeleteClick(event.cocktail) //
            }
            OnReady -> loadContent()
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(Loading)
        viewModelScope.launch {
            val customs = repository.loadAll()
            if (customs == null) {
                states.postValue(Error(CustomListEmpty))
            } else {
                customList = customs.map {
                    CustomDrinkUI(
                        drinkName = it.name,
                        drinkImage = it.image,
                        drinkId = it.id,
                        drinkType = it.category
                    )
                }
                val content = Content(customList!!)
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
