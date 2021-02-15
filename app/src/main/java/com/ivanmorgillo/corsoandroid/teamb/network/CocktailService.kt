package com.ivanmorgillo.corsoandroid.teamb.network

import DetailCocktailDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {
    @GET("filter.php?c=Cocktail")
    suspend fun loadCocktails(): CocktailDTO

    @GET("lookup.php")
    suspend fun loadDetailCocktails(@Query("i") i: String): DetailCocktailDTO
}
