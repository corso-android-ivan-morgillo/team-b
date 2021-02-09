package com.ivanmorgillo.corsoandroid.teamb.network

import retrofit2.http.GET

interface CocktailService {
    @GET("filter.php?c=Cocktail")
    suspend fun loadCocktails(): CocktailDTO
}
