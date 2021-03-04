package com.ivanmorgillo.corsoandroid.teamb.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

interface Tracking {
    fun logEvent(eventName: String)
    fun logEvent(eventName: String, eventType: Bundle?)
}

class TrackingImpl : Tracking {
    override fun logEvent(eventName: String) {
        Firebase.analytics.logEvent(eventName, null)
    }

    override fun logEvent(eventName: String, eventType: Bundle?) {
        Firebase.analytics.logEvent(eventName, eventType)
    }
}
