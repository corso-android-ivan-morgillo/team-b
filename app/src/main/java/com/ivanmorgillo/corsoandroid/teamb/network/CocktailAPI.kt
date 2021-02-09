package com.ivanmorgillo.corsoandroid.teamb.network

import com.ivanmorgillo.corsoandroid.teamb.Cocktail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CocktailAPI {
    private val service: CocktailService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(CocktailService::class.java)
    }

    suspend fun loadCocktails(): List<Cocktail> {
        val cocktailList = service.loadCocktails()
        return cocktailList.drinks.map {
            Cocktail(
                name = it.strDrink,
                image = it.strDrinkThumb,
                idMeal = it.idDrink
            )
        }
    }
}
