package com.ivanmorgillo.corsoandroid.teamb

import com.ivanmorgillo.corsoandroid.teamb.network.CocktailAPI
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    ->
    /* creiamo un repository Singleton Pattern */
    single<CocktailRepository> {
        CocktailRepositoryImpl(api = get())
    }
    single<CocktailAPI> {
        CocktailAPI()
    }
    single<Tracking> {
        TrackingImpl()
    }
    // Creiamo un oggetto di tipo MainViewModel
    this.viewModel<MainViewModel> { MainViewModel(repository = get(), tracking = get()) }
}
