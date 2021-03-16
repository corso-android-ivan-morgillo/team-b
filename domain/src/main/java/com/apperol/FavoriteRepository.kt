package com.apperol

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface FavoriteRepository {
    suspend fun save(favorite: Detail, isFavorite: Boolean): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavorite(id: Long): Boolean
    suspend fun loadAll(): List<Favorite>?
}

class FavoriteRepositoryImpl : FavoriteRepository {
    private val firestore by lazy { Firebase.firestore }

    override suspend fun save(favorite: Detail, isFavorite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            "id" to favorite.id,
            "name" to favorite.name,
            "image" to favorite.image,
            "category" to favorite.category
        )
        firestore.collection("favourites").add(favouriteMap).await()
        return true
    }

    override suspend fun delete(id: Long) = true

    override suspend fun isFavorite(id: Long): Boolean = false

    override suspend fun loadAll(): List<Favorite>? = emptyList()
}

data class Favorite(
    val name: String,
    val image: String,
    val id: Long,
    val category: String
)
