package com.ivanmorgillo.corsoandroid.teamb.utils

import com.apperol.networking.CocktailAPI
import com.ivanmorgillo.corsoandroid.teamb.MainActivityViewModel
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teamb.home.HomeViewModel
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailApiImpl
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamb.search.SearchViewModel
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingViewModel
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingsRepository
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /* creiamo un repository Singleton Pattern */
    single<CocktailRepository> {
        CocktailRepositoryImpl(api = get())
    }

    single<CocktailAPI> {
        CocktailApiImpl()
    }

    single<Tracking> {
        TrackingImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    // Creiamo un oggetto di tipo MainViewModel
    viewModel { HomeViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get(), tracking = get()) }
    viewModel { SearchViewModel(repository = get(), tracking = get()) }
    viewModel { MainActivityViewModel(settingsrepository = get(), tracking = get()) }
    viewModel { SettingViewModel(settingsRepository = get(), tracking = get()) }
}
