package com.apperol

sealed class LoadCocktailResult {
    data class Success(val cocktails: List<Cocktail>) : LoadCocktailResult()
    data class Failure(val error: LoadCocktailError) : LoadCocktailResult()
}
