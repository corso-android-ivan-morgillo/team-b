package com.apperol.networking

import com.ivanmorgillo.corsoandroid.teamb.network.LoadCategoriesResult
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.network.LoadDetailCocktailResult
import com.ivanmorgillo.corsoandroid.teamb.network.LoadSearchCocktailResult

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
