package com.ivanmorgillo.corsoandroid.teamb

import com.ivanmorgillo.corsoandroid.teamb.network.CocktailAPI
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /* creiamo un repository Singleton Pattern */
    single<CocktailRepository> {
        CocktailRepositoryImpl(api = get())
    }
    single<CocktailAPI> {
        CocktailAPI()
    }
    // Creiamo un oggetto di tipo MainViewModel
    viewModel { MainViewModel(repository = get()) }
}
