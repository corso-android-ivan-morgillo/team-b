package com.ivanmorgillo.corsoandroid.teamb.network

import com.apperol.networking.CocktailAPI

interface CocktailRepository {
    suspend fun loadDrinks(category: String): LoadCocktailResult
    suspend fun loadDetailCocktails(cocktailId: Long): LoadDetailCocktailResult
    suspend fun loadRandomDetailCocktails(): LoadDetailCocktailResult
    suspend fun loadSearchCocktails(query: String): LoadSearchCocktailResult
    suspend fun loadCategories(): LoadCategoriesResult
}

/* Implementazione dell'interfaccia definita prima */
class CocktailRepositoryImpl(private val api: CocktailAPI) : CocktailRepository {
    override suspend fun loadDrinks(category: String): LoadCocktailResult {
        return api.loadDrinks(category)
    }

    override suspend fun loadDetailCocktails(cocktailId: Long): LoadDetailCocktailResult {
        return api.loadDetailCocktails(cocktailId)
    }

    override suspend fun loadRandomDetailCocktails(): LoadDetailCocktailResult {
        return api.loadRandomDetailCocktails()
    }

    override suspend fun loadSearchCocktails(query: String): LoadSearchCocktailResult {
        return api.loadSearchCocktails(query)
    }

    override suspend fun loadCategories(): LoadCategoriesResult {
        return api.loadCategories()
    }
}
