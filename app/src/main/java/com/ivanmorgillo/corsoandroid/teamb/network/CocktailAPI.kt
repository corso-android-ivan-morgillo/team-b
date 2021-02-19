package com.ivanmorgillo.corsoandroid.teamb.network

import DetailCocktailDTO
import com.ivanmorgillo.corsoandroid.teamb.Cocktail
import com.ivanmorgillo.corsoandroid.teamb.Detail
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

    @Suppress("TooGenericExceptionCaught")
    suspend fun loadDetailCocktails(idDrink: Long): LoadDetailCocktailResult {
        try {
            val detailCocktailList = service.loadDetailCocktails(idDrink.toString())
            val details = detailCocktailList.details
            return if (details.isEmpty()) {
                LoadDetailCocktailResult.Failure(LoadCocktailError.NoDetailFound)
            } else {
                val domainDetails = details.toDomain()
                if (domainDetails == null) {
                    Timber.e(Throwable("Invalid cocktail ID"))
                    LoadDetailCocktailResult.Failure(LoadCocktailError.NoDetailFound)
                } else {
                    LoadDetailCocktailResult.Success(domainDetails)
                }
            }
        } catch (e: IOException) { // no internet
            return LoadDetailCocktailResult.Failure(NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadDetailCocktailResult.Failure(SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadCocktail")
            return LoadDetailCocktailResult.Failure(ServerError)
        }
    }
}

private fun resolveMeasures(ingredient: String?, measure: String?): String? {
    return when {
        ingredient != null && measure != null -> measure
        ingredient != null && measure == null -> "q.b"
        else -> null
    }
}

private fun DetailCocktailDTO.Drink.measurementsList() = listOfNotNull(
    resolveMeasures(strIngredient1, strMeasure1),
    resolveMeasures(strIngredient2, strMeasure2),
    resolveMeasures(strIngredient3, strMeasure3),
    resolveMeasures(strIngredient4, strMeasure4),
    resolveMeasures(strIngredient5, strMeasure5),
    resolveMeasures(strIngredient6, strMeasure6),
    resolveMeasures(strIngredient7, strMeasure7),
    resolveMeasures(strIngredient8, strMeasure8),
    resolveMeasures(strIngredient9, strMeasure9),
    resolveMeasures(strIngredient10, strMeasure10),
    resolveMeasures(strIngredient11, strMeasure11),
    resolveMeasures(strIngredient12, strMeasure12),
    resolveMeasures(strIngredient13, strMeasure13),
    resolveMeasures(strIngredient14, strMeasure14),
    resolveMeasures(strIngredient15, strMeasure15),
)

private fun DetailCocktailDTO.Drink.ingredientsList() = listOfNotNull(
    strIngredient1,
    strIngredient2,
    strIngredient3,
    strIngredient4,
    strIngredient5,
    strIngredient6,
    strIngredient7,
    strIngredient8,
    strIngredient9,
    strIngredient10,
    strIngredient11,
    strIngredient12,
    strIngredient13,
    strIngredient14,
    strIngredient15,
)

private fun List<DetailCocktailDTO.Drink>.toDomain(): Detail? {
    val first = this.firstOrNull() ?: return null
    val id = first.idDrink.toLongOrNull()
    val alcolCat: Boolean = first.strAlcoholic.equals("Alcoholic")
    val video: String? = first.strVideo?.split("https://www.youtube.com/watch?v=")?.first()
    val ingredientsList = first.ingredientsList()
    val measurementsList = first.measurementsList()
    val ingredients = ingredientsList
        .mapIndexed { index, ingredientName ->
            Ingredient(ingredientName, measurementsList[index])
        }
    return if (id != null) {
        Detail(
            name = first.strDrink,
            image = first.strDrinkThumb,
            idDrink = id,
            isAlcoholic = alcolCat,
            glass = first.strGlass,
            ingredients = ingredients,
            youtubeLink = video, // se si porta dietro un valore null, che succede?
            instructions = first.strInstructions,
        )
    } else {
        null
    }
}

sealed class LoadCocktailError {
    object NoCocktailFound : LoadCocktailError()
    object NoInternet : LoadCocktailError()
    object SlowInternet : LoadCocktailError()
    object ServerError : LoadCocktailError()
    object NoDetailFound : LoadCocktailError()
}

sealed class LoadCocktailResult {
    data class Success(val cocktails: List<Cocktail>) : LoadCocktailResult()
    data class Failure(val error: LoadCocktailError) : LoadCocktailResult()
}

sealed class LoadDetailCocktailResult {
    data class Success(val details: Detail) : LoadDetailCocktailResult()
    data class Failure(val error: LoadCocktailError) : LoadDetailCocktailResult()
}

data class Ingredient(val name: String, val quantity: String)
