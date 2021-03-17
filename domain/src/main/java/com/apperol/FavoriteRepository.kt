package com.apperol

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface FavoriteRepository {
    suspend fun save(favorite: Detail): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavorite(id: Long): Boolean
    suspend fun loadAll(): List<Favorite>?
}

class FavoriteRepositoryImpl(private val firestore: FirebaseFirestore) : FavoriteRepository {

    private val favouritesCollection by lazy {
        firestore.collection("favourites-${getUid()}")
    }

    private fun getUid() = Firebase.auth.currentUser.uid
    override suspend fun save(favorite: Detail): Boolean {
        val favouriteMap = hashMapOf(
            "id" to favorite.id,
            "name" to favorite.name,
            "image" to favorite.image,
            "category" to favorite.category,
            // "userID" to getUid()

        )
        favouritesCollection.document(favorite.id.toString()).set(favouriteMap).await()
        return true
    }

    override suspend fun delete(id: Long): Boolean {
        favouritesCollection.document(id.toString()).delete().await()
        return true
    }

    override suspend fun isFavorite(id: Long): Boolean {
        val x = favouritesCollection.document(id.toString()).get().await()
        return x.exists()
    }

    override suspend fun loadAll(): List<Favorite>? {
        val favouritesList = favouritesCollection
            // .whereEqualTo("userID", getUid())
            .get()
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
        return if (favouritesList.isEmpty()) {
            null
        } else {
            favouritesList
        }
    }
}

data class Favorite(
    val name: String,
    val image: String,
    val id: Long,
    val category: String
)
