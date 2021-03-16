package com.ivanmorgillo.corsoandroid.teamb

import android.app.Application
import android.os.StrictMode
import com.apperol.BuildConfig
import com.apperol.networkingKoinModule
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingsRepository
import com.ivanmorgillo.corsoandroid.teamb.utils.CrashReportingTree
import com.ivanmorgillo.corsoandroid.teamb.utils.LineNumberDebugTree
import com.ivanmorgillo.corsoandroid.teamb.utils.appModule
import com.ivanmorgillo.corsoandroid.teamb.utils.disableDarkMode
import com.ivanmorgillo.corsoandroid.teamb.utils.enableDarkMode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, networkingKoinModule)
        }
        setupTimber()

        val settingRepository: SettingsRepository = get()

        GlobalScope.launch {
            if (settingRepository.isThemeSwitchOn()) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog() // log in logcat
                    .build()
            )
        }
    }
}
