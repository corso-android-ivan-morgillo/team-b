package com.apperol

import com.apperol.DetailLoadCocktailError.NoDetailFound
import com.apperol.LoadDetailCocktailResult.Failure
import com.apperol.LoadDetailCocktailResult.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CocktailRepository {
    suspend fun loadDrinks(category: String): LoadCocktailResult
    suspend fun loadDetailCocktails(cocktailId: Long, isCustom: Boolean): LoadDetailCocktailResult
    suspend fun loadRandomDetailCocktails(): LoadDetailCocktailResult
    suspend fun loadSearchCocktails(query: String): LoadSearchCocktailResult
    suspend fun loadCategories(): LoadCategoriesResult
}

/* Implementazione dell'interfaccia definita prima */
class CocktailRepositoryImpl(
    private val api: CocktailAPI,
    private val customDrinkRepository: CustomDrinkRepository
) : CocktailRepository {
    override suspend fun loadDrinks(category: String): LoadCocktailResult {
        return api.loadDrinks(category)
    }

    override suspend fun loadDetailCocktails(
        cocktailId: Long,
        isCustom: Boolean
    ): LoadDetailCocktailResult = withContext(Dispatchers.IO) {
        when {
            isCustom -> {
                val detail = customDrinkRepository.loadById(cocktailId)
                if (detail == null) Failure(NoDetailFound)
                else Success(detail)
            }
            else -> api.loadDetailCocktails(cocktailId)
        }
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
