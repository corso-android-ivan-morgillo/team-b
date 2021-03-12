package com.apperol

sealed class LoadSearchCocktailResult {
    data class Success(val details: List<Search>) : LoadSearchCocktailResult()
    data class Failure(val error: SearchLoadCocktailError) : LoadSearchCocktailResult()
}
