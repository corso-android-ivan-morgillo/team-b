package com.ivanmorgillo.corsoandroid.teamb.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

sealed class Screens {
    abstract val className: String
    abstract val name: String

    object Home : Screens() {
        override val className: String = "Home Fragment"
        override val name: String = "Home"
    }

    object Detail : Screens() {
        override val className: String = "Detail Fragment"
        override val name: String = "Detail"
    }

    object Setting : Screens() {
        override val className: String = "Setting Fragment"
        override val name: String = "Setting"
    }

    object Search : Screens() {
        override val className: String = "Search Fragment"
        override val name: String = "Search"
    }

    object Random : Screens() {
        override val className: String = "Random Fragment"
        override val name: String = "Random"
    }

    object Favorite : Screens() {
        override val className: String = "Favorite Fragment"
        override val name: String = "Favorite"
    }
}

interface Tracking {
    fun logEvent(eventName: String)
    fun logEvent(eventName: String, eventType: Bundle?)
    fun logScreen(screen: Screens)
}

class TrackingImpl : Tracking {
    override fun logEvent(eventName: String) {
        Firebase.analytics.logEvent(eventName, null)
    }

    override fun logEvent(eventName: String, eventType: Bundle?) {
        Firebase.analytics.logEvent(eventName, eventType)
    }

    override fun logScreen(screen: Screens) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen.name)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screen.className)
        }
    }
}
