package com.ivanmorgillo.corsoandroid.teamb.utils

import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.ivanmorgillo.corsoandroid.teamb.MainActivity
import timber.log.Timber

private fun getAdSize(activity: MainActivity, adViewContainer: FrameLayout): AdSize {
    val display = activity.windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = outMetrics.density

    var adWidthPixels = adViewContainer.width.toFloat()
    if (adWidthPixels == 0f) {
        adWidthPixels = outMetrics.widthPixels.toFloat()
    }

    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
}

fun setupAds(activity: MainActivity, adViewContainer: FrameLayout) {
    MobileAds.initialize(activity) {}
    val adView = AdView(activity)
    adViewContainer.addView(adView)
    adView.adListener = object : AdListener() {
        override fun onAdLoaded() = Unit

        override fun onAdFailedToLoad(adError: LoadAdError) {
            Timber.e(Throwable("Cannot Load Ad: ${adError.code}"))
        }

        override fun onAdOpened() = Unit

        override fun onAdClicked() = Unit

        override fun onAdLeftApplication() = Unit

        override fun onAdClosed() = Unit
    }
    adView.adUnitId = "ca-app-pub-4984877505456457/4180942455"

    adView.adSize = getAdSize(activity, adViewContainer)

    // Create an ad request. Check your logcat output for the hashed device ID to
    // get test ads on a physical device, e.g.,
    // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
    val requestConfiguration = RequestConfiguration
        .Builder()
        .setTestDeviceIds(listOf("1DBF4DCCF4D941F406A3311829733E08"))
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    val adRequest = AdRequest.Builder().build()
    // Start loading the ad in the background.
    adView.loadAd(adRequest)
}
