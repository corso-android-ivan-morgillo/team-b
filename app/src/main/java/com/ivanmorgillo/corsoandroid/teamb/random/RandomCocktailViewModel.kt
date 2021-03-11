package com.ivanmorgillo.corsoandroid.teamb.random

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailScreenItems
import com.ivanmorgillo.corsoandroid.teamb.utils.Screens
import com.ivanmorgillo.corsoandroid.teamb.utils.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teamb.utils.Tracking
import timber.log.Timber

class RandomCocktailViewModel(
    private val tracking: Tracking
) : ViewModel() {
    val states = MutableLiveData<RandomScreenStates>()
    val action = SingleLiveEvent<RandomScreenAction>()

    init {
        tracking.logScreen(Screens.Random)
    }

    fun send(event: RandomScreenEvents) {
        when (event) {
            is RandomScreenEvents.OnReady -> Timber.d("OnReady in RandomCocktailViewModel")
            is RandomScreenEvents.OnShaking -> {
                tracking.logEvent("shaking_action_performed")
                action.postValue(RandomScreenAction.NavigateToDetail)
            }
        }
    }
}

sealed class RandomScreenAction {
    object NavigateToDetail : RandomScreenAction()
}

sealed class RandomScreenStates {
    object Loading : RandomScreenStates()
    data class Error(val error: RandomErrorStates) : RandomScreenStates()
    data class Content(val details: List<DetailScreenItems>) : RandomScreenStates()
}

sealed class RandomScreenEvents {
    data class OnReady(val id: Long) : RandomScreenEvents()
    object OnShaking : RandomScreenEvents()
}

sealed class RandomErrorStates {
    object ShowNoInternetMessage : RandomErrorStates()
    object ShowNoCocktailFound : RandomErrorStates()
    object ShowServerError : RandomErrorStates()
    object ShowSlowInternet : RandomErrorStates()
    object ShowNoDetailFound : RandomErrorStates()
}
