package com.apperol

interface CocktailAPI {
    @Suppress("TooGenericExceptionCaught")
    suspend fun loadDrinks(category: String): LoadCocktailResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadRandomDetailCocktails(): LoadDetailCocktailResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadDetailCocktails(idDrink: Long): LoadDetailCocktailResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadSearchCocktails(query: String): LoadSearchCocktailResult

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadCategories(): LoadCategoriesResult
}
