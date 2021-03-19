package com.apperol.networking

import com.apperol.CategoriesError
import com.apperol.Category
import com.apperol.Cocktail
import com.apperol.CocktailAPI
import com.apperol.Detail
import com.apperol.DetailLoadCocktailError
import com.apperol.Ingredient
import com.apperol.LoadCategoriesResult
import com.apperol.LoadCocktailError.NoCocktailFound
import com.apperol.LoadCocktailError.NoInternet
import com.apperol.LoadCocktailError.ServerError
import com.apperol.LoadCocktailError.SlowInternet
import com.apperol.LoadCocktailResult
import com.apperol.LoadCocktailResult.Failure
import com.apperol.LoadCocktailResult.Success
import com.apperol.LoadDetailCocktailResult
import com.apperol.LoadSearchCocktailResult
import com.apperol.Search
import com.apperol.SearchLoadCocktailError
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class CocktailApiImpl : CocktailAPI {
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
    override suspend fun loadDrinks(category: String): LoadCocktailResult {
        // try-catch per gestire errore chiamata di rete
        try {
            val cocktailList = service.loadDrinks(category)
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

    private fun DrinkDTO.Drink.toDomain(): Cocktail? {
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
    override suspend fun loadRandomDetailCocktails(): LoadDetailCocktailResult {
        try {
            val detailCocktailList = service.loadRandomDetailCocktails()
            val details = detailCocktailList.details
            return if (details.isNullOrEmpty()) {
                LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoDetailFound)
            } else {
                val domainDetails = details.toDomain()
                if (domainDetails == null) {
                    Timber.e(Throwable("Invalid cocktail ID"))
                    LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoDetailFound)
                } else {
                    LoadDetailCocktailResult.Success(domainDetails)
                }
            }
        } catch (e: IOException) { // no internet
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadRandomCocktail")
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.ServerError)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadDetailCocktails(idDrink: Long): LoadDetailCocktailResult {
        try {
            val detailCocktailList = service.loadDetailCocktails(idDrink.toString())
            val details = detailCocktailList.details
            return if (details.isNullOrEmpty()) {
                LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoDetailFound)
            } else {
                val domainDetails = details.toDomain()
                if (domainDetails == null) {
                    Timber.e(Throwable("Invalid cocktail ID"))
                    LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoDetailFound)
                } else {
                    LoadDetailCocktailResult.Success(domainDetails)
                }
            }
        } catch (e: IOException) { // no internet
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadCocktail")
            return LoadDetailCocktailResult.Failure(DetailLoadCocktailError.ServerError)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadSearchCocktails(query: String): LoadSearchCocktailResult {
        try {
            val detailCocktailList = service.loadSearchCocktails(query)
            return when (detailCocktailList.details) {
                null -> {
                    LoadSearchCocktailResult.Failure(SearchLoadCocktailError.NoCocktailFound)
                }
                else -> {
                    val details = detailCocktailList.details.mapNotNull {
                        it.toDomainSearch()
                    }
                    return if (details.isEmpty()) {
                        LoadSearchCocktailResult.Failure(SearchLoadCocktailError.NoCocktailFound)
                    } else {
                        LoadSearchCocktailResult.Success(details)
                    }
                }
            }
        } catch (e: IOException) { // no internet
            return LoadSearchCocktailResult.Failure(SearchLoadCocktailError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadSearchCocktailResult.Failure(SearchLoadCocktailError.SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadSearchCocktail")
            return LoadSearchCocktailResult.Failure(SearchLoadCocktailError.ServerError)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadCategories(): LoadCategoriesResult {
        try {
            val categoriesList = service.loadCategories()
            val categories = categoriesList.categories.map {
                it.toDomainCategories()
            }
            return if (categories.isEmpty()) {
                LoadCategoriesResult.Failure(CategoriesError.NoCategoriesFound)
            } else {
                LoadCategoriesResult.Success(categories)
            }
        } catch (e: IOException) { // no internet
            return LoadCategoriesResult.Failure(CategoriesError.NoInternet)
        } catch (e: SocketTimeoutException) {
            return LoadCategoriesResult.Failure(CategoriesError.SlowInternet)
        } catch (e: Exception) {
            Timber.e(e, "Generic Exception on LoadCategories")
            return LoadCategoriesResult.Failure(CategoriesError.ServerError)
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

private fun DetailCocktailDTO.DrinkDTO.measurementsList() = listOfNotNull(
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

private fun DetailCocktailDTO.DrinkDTO.ingredientsList() = listOfNotNull(
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

private fun List<DetailCocktailDTO.DrinkDTO>.toDomain(): Detail? {
    val first = this.firstOrNull() ?: return null
    val id = first.idDrink.toLongOrNull()
    val alcolCat: Boolean = first.strAlcoholic == "Alcoholic"
    val video: String? = first.strVideo
    val instructions = instructionsLanguage(first)
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
            id = id,
            isAlcoholic = alcolCat,
            glass = first.strGlass,
            ingredients = ingredients,
            youtubeLink = video,
            instructions = instructions,
            category = first.strCategory
        )
    } else {
        null
    }
}

fun instructionsLanguage(first: DetailCocktailDTO.DrinkDTO): String {
    val language = Locale.getDefault().displayLanguage
    return if (language == "italiano" && first.strInstructionsIT != null) {
        first.strInstructionsIT
    } else {
        first.strInstructions
    }
}

private fun DetailCocktailDTO.DrinkDTO.toDomainSearch(): Search? {
    val id = idDrink.toLongOrNull()
    return if (id != null) {
        Search(
            name = strDrink,
            image = strDrinkThumb,
            idDrink = id,
            category = strCategory,
            alcoholic = strAlcoholic
        )
    } else {
        null
    }
}

private fun CategoriesDTO.CategoryDTO.toDomainCategories(): Category {
    return Category(categoryName = strCategory)
}
