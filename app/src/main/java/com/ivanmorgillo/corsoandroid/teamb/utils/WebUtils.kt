package com.ivanmorgillo.corsoandroid.teamb.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

fun openNewTabWindow(urls: String, context: Context) {
    val uris = Uri.parse(urls)
    val intents = Intent(Intent.ACTION_VIEW, uris)
    val b = Bundle()
    b.putBoolean("new_window", true)
    intents.putExtras(b)
    context.startActivity(intents)
}
