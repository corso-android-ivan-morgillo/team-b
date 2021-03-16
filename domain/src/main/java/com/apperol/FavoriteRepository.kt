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
    private val favouritesCollection = firestore.collection("favourites")

    override suspend fun save(favorite: Detail, isFavorite: Boolean): Boolean {
        val favouriteMap = hashMapOf(
            "id" to favorite.id,
            "name" to favorite.name,
            "image" to favorite.image,
            "category" to favorite.category
        )
        favouritesCollection.add(favouriteMap).await()
        return true
    }

    override suspend fun delete(id: Long) = true

    override suspend fun isFavorite(id: Long): Boolean = false

    override suspend fun loadAll(): List<Favorite>? {
        return favouritesCollection.get()
            .await()
            .documents
            .map {
                val name = it["name"] as String
                val image = it["image"] as String
                val category = it["category"] as String
                val id = it["id"] as Long
                Favorite(
                    name = name,
                    image = image,
                    id = id,
                    category = category
                )
            }
    }
}

data class Favorite(
    val name: String,
    val image: String,
    val id: Long,
    val category: String
)
