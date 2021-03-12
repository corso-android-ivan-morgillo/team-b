package com.apperol

sealed class LoadCocktailError {
    object NoCocktailFound : LoadCocktailError()
    object NoInternet : LoadCocktailError()
    object SlowInternet : LoadCocktailError()
    object ServerError : LoadCocktailError()
}
