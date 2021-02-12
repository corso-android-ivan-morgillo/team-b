package com.ivanmorgillo.corsoandroid.teamb.network

import com.ivanmorgillo.corsoandroid.teamb.Cocktail
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoCocktailFound
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.NoInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.ServerError
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailError.SlowInternet
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Failure
import com.ivanmorgillo.corsoandroid.teamb.network.LoadCocktailResult.Success
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

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

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadCocktails(): LoadCocktailResult {
        // try-catch per gestire errore chiamata di rete
        try {
            val cocktailList = service.loadCocktails()
            val cocktails = cocktailList.drinks.mapNotNull {
                it.toDomain()
            }
            return if (cocktails.isEmpty()) {
                Failure(NoCocktailFound)
            } else {
                Success(cocktails)
            }
        } catch (e: IOException) { // no internet
            return Failure(NoInternet)
        } catch (e: SocketTimeoutException) {
            return Failure(SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadCocktail")
            return Failure(ServerError)
        }
    }

    private fun CocktailDTO.Drink.toDomain(): Cocktail? {
        val id = idDrink.toLongOrNull()
        return if (id != null) {
            Cocktail(
                name = strDrink,
                image = strDrinkThumb,
                idDrink = id
            )
        } else {
            null
        }
    }
}

sealed class LoadCocktailError {
    object NoCocktailFound : LoadCocktailError()
    object NoInternet : LoadCocktailError()
    object SlowInternet : LoadCocktailError()
    object ServerError : LoadCocktailError()
}

sealed class LoadCocktailResult {
    data class Success(val cocktails: List<Cocktail>) : LoadCocktailResult()
    data class Failure(val error: LoadCocktailError) : LoadCocktailResult()
}
