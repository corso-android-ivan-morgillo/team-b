package com.apperol

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FavoriteRepository {
    suspend fun save(favorite: Detail, isFavorite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavorite(id: Long): Boolean
    suspend fun loadAll(): List<Favorite>
}

class FavoriteRepositoryImpl(
    val context: Context,
    private val gson: Gson
) : FavoriteRepository {
    private val storage: SharedPreferences by lazy {
        context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun save(favorite: Detail, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        if (isFavorite) {
            val cocktailEntity = CocktailEntity(
                name = favorite.name,
                image = favorite.image,
                id = favorite.id,
                category = favorite.category
            )
            val serializedCocktail = gson.toJson(cocktailEntity)
            storage.edit().putString(favorite.id.toString(), serializedCocktail).commit()
        } else {
            storage.edit().remove(favorite.id.toString()).commit()
        }
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        storage.edit().remove(id.toString()).commit()
    }

    override suspend fun isFavorite(id: Long): Boolean = withContext(Dispatchers.IO) {
        val maybeFavorite = storage.getString(id.toString(), null)
        maybeFavorite != null
    }

    override suspend fun loadAll(): List<Favorite> = withContext(Dispatchers.IO) {
        storage.all
            .values
            .map {
                it as String
            }
            .map {
                gson.fromJson(it, CocktailEntity::class.java)
            }
            .map {
                Favorite(
                    name = it.name,
                    image = it.image,
                    id = it.id,
                    category = it.category
                )
            }
    }
}

data class CocktailEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("category")
    val category: String
)

data class Favorite(
    val name: String,
    val image: String,
    val id: Long,
    val category: String
)
