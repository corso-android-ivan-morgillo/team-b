package com.apperol

sealed class DetailLoadCocktailError {
    object NoInternet : DetailLoadCocktailError()
    object SlowInternet : DetailLoadCocktailError()
    object ServerError : DetailLoadCocktailError()
    object NoDetailFound : DetailLoadCocktailError()
}
