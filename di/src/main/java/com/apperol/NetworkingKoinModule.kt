package com.apperol

import com.apperol.networking.CocktailApiImpl
import org.koin.dsl.module

val networkingKoinModule = module {
    single<CocktailAPI> {
        CocktailApiImpl()
    }
}
