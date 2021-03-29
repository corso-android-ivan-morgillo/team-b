package com.ivanmorgillo.corsoandroid.teamb.utils

import com.apperol.AuthenticationManager
import com.apperol.AuthenticationManagerImpl
import com.apperol.CocktailRepository
import com.apperol.CocktailRepositoryImpl
import com.apperol.CustomDrinkRepository
import com.apperol.CustomDrinkRepositoryImpl
import com.apperol.FavoriteRepository
import com.apperol.FavoriteRepositoryImpl
import com.ivanmorgillo.corsoandroid.teamb.MainActivityViewModel
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomFormViewModel
import com.ivanmorgillo.corsoandroid.teamb.custom.CustomListViewModel
import com.ivanmorgillo.corsoandroid.teamb.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teamb.favorites.FavoriteViewModel
import com.ivanmorgillo.corsoandroid.teamb.home.HomeViewModel
import com.ivanmorgillo.corsoandroid.teamb.random.RandomCocktailViewModel
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
        CocktailRepositoryImpl(api = get(), customDrinkRepository = get())
    }
    single<Tracking> {
        TrackingImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(androidContext())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(firestore = get(), authenticationManager = get())
    }

    single<AuthenticationManager> {
        AuthenticationManagerImpl()
    }

    single<CustomDrinkRepository> {
        CustomDrinkRepositoryImpl(firestore = get(), authenticationManager = get())
    }

    // Creiamo un oggetto di tipo MainViewModel
    viewModel { HomeViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get(), favoriteRepository = get(), tracking = get()) }
    viewModel { SearchViewModel(repository = get(), tracking = get()) }
    viewModel { MainActivityViewModel(settingsrepository = get(), tracking = get()) }
    viewModel { SettingViewModel(settingsRepository = get(), tracking = get()) }
    viewModel { RandomCocktailViewModel(tracking = get()) }
    viewModel { FavoriteViewModel(tracking = get(), repository = get()) }
    viewModel { CustomFormViewModel(customDrinkRepository = get(), tracking = get()) }
    viewModel { CustomListViewModel(tracking = get(), customRepository = get()) }
}
