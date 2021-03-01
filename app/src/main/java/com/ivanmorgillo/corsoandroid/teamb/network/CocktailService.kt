package com.ivanmorgillo.corsoandroid.teamb.network

import DetailCocktailDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {
    @GET("filter.php?c=Cocktail")
    suspend fun loadCocktails(): CocktailDTO

    @GET("lookup.php")
    suspend fun loadDetailCocktails(@Query("i") i: String): DetailCocktailDTO

    @GET("random.php")
    suspend fun loadRandomDetailCocktails(): DetailCocktailDTO

    @GET("search.php")
    suspend fun loadSearchCocktails(@Query("s") s: String): DetailCocktailDTO

    @GET("list.php?c=list")
    suspend fun loadCategories(): CategoriesDTO
}
