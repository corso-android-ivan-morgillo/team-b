package com.ivanmorgillo.corsoandroid.teamb.utils

import com.ivanmorgillo.corsoandroid.teamb.MainActivityViewModel
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teamb.home.HomeViewModel
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailAPI
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailApiImpl
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepository
import com.ivanmorgillo.corsoandroid.teamb.network.CocktailRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamb.search.SearchViewModel
import com.ivanmorgillo.corsoandroid.teamb.settings.SettingViewModel
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

    // Creiamo un oggetto di tipo MainViewModel
    viewModel { HomeViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get(), tracking = get()) }
    viewModel { SearchViewModel(repository = get(), tracking = get()) }
    viewModel { MainActivityViewModel(repository = get(), tracking = get()) }
    viewModel { SettingViewModel(repository = get(), tracking = get()) }
}
