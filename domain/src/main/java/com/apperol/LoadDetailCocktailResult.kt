package com.apperol

sealed class LoadDetailCocktailResult {
    data class Success(val details: Detail) : LoadDetailCocktailResult()
    data class Failure(val error: DetailLoadCocktailError) : LoadDetailCocktailResult()
}
