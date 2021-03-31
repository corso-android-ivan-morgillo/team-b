package com.apperol

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FavoriteRepository {
    suspend fun save(favorite: Detail): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun isFavorite(id: Long): Boolean
    suspend fun loadAll(): List<Favorite>?
}

class FavoriteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authenticationManager: AuthenticationManager,
) : FavoriteRepository {

    private val favouritesCollection by lazy {
        firestore.collection("favourites")
    }

    override suspend fun save(favorite: Detail): Boolean {
        val uid = authenticationManager.getUId() ?: return false
        val favouriteMap = hashMapOf(
            "id" to favorite.id,
            "name" to favorite.name,
            "image" to favorite.image,
            "category" to favorite.category,
            "userid" to uid
        )
        favouritesCollection.document("$uid-${favorite.id}").set(favouriteMap).await()
        return true
    }

    override suspend fun delete(id: Long): Boolean {
        val uid = authenticationManager.getUId() ?: return false
        favouritesCollection
            .document("$uid-$id")
            .delete()
            .await()
        return true
    }

    override suspend fun isFavorite(id: Long): Boolean {
        if (authenticationManager.isUserLoggedIn()) {
            val uid = authenticationManager.getUId() ?: return false
            try {
                val x = favouritesCollection
                    .document("$uid-$id")
                    .get()
                    .await()
                return x.exists()
            } catch (e: Exception) {
                return false
            }
        }
        return false
    }

    override suspend fun loadAll(): List<Favorite>? {
        val uid = authenticationManager.getUId() ?: return null
        val favouritesList = favouritesCollection
            .whereEqualTo("userid", uid)
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
    val category: String,
)
