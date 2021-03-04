package com.ivanmorgillo.corsoandroid.teamb.network

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

data class Cocktail(val name: String, val image: String, val idDrink: Long)

data class Detail(
    val name: String,
    val image: String,
    val idDrink: Long,
    val isAlcoholic: Boolean,
    val glass: String,
    val ingredients: List<Ingredient>,
    val youtubeLink: String?,
    val instructions: String,
)

data class Search(
    val name: String,
    val image: String,
    val idDrink: Long,
    val alcoholic: String,
    val category: String
)
