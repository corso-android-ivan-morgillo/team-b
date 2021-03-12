package com.apperol.networking

import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {
    @GET("filter.php")
    suspend fun loadDrinks(@Query("c") c: String): DrinkDTO

    @GET("lookup.php")
    suspend fun loadDetailCocktails(@Query("i") i: String): DetailCocktailDTO

    @GET("random.php")
    suspend fun loadRandomDetailCocktails(): DetailCocktailDTO

    @GET("search.php")
    suspend fun loadSearchCocktails(@Query("s") s: String): DetailCocktailDTO

    @GET("list.php?c=list")
    suspend fun loadCategories(): CategoriesDTO
}
