package com.apperol

sealed class SearchLoadCocktailError {
    object NoCocktailFound : SearchLoadCocktailError()
    object NoInternet : SearchLoadCocktailError()
    object SlowInternet : SearchLoadCocktailError()
    object ServerError : SearchLoadCocktailError()
}
