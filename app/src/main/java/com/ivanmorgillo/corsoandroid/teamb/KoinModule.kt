package com.ivanmorgillo.corsoandroid.teamb

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /* creiamo un repository Singleton Pattern */
    single<CocktailRepository> {
        CocktailRepositoryImpl()
    }
    // Creiamo un oggetto di tipo MainViewModel
    viewModel { MainViewModel(repository = get()) }
}
