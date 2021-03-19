package com.apperol

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface CustomDrinkRepository {
    suspend fun save(customDrink: Detail): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun loadAll(): List<Detail>?
}

class CustomDrinkRepositoryImpl(private val firestore: FirebaseFirestore) : CustomDrinkRepository {
    private val customCollection by lazy {
        firestore.collection("User - ${getUid()}")
    }

    private fun getUid() = Firebase.auth.currentUser.uid
    override suspend fun save(customDrink: Detail): Boolean {
        val id = generateRandomUserId() + 1
        val customMap = hashMapOf(
            "id" to id,
            "name" to customDrink.name,
            "image" to customDrink.image,
            "category" to customDrink.category,
            "glass" to customDrink.glass,
            "alcoholic" to customDrink.isAlcoholic,
            "ingredients" to customDrink.ingredients,
            "instructions" to customDrink.instructions,
        )
        customCollection.document("custom-$id").set(customMap).await()
        return true
    }

    override suspend fun delete(id: Long): Boolean {
        customCollection.document("custom-$id").delete().await()
        return true
    }

    override suspend fun loadAll(): List<Detail>? {
        val customList = customCollection
            .get()
            .await()
            .documents
            .filter {
                it.contains("custom-")
            }.map {
                val name = it["name"] as String
                val image = it["image"] as String
                val category = it["category"] as String
                val id = it["id"] as Long
                val glass = it["glass"] as String
                val alcoholic = it["alcoholic"] as Boolean
                val ingredients = it["ingredients"] as List<Ingredient>
                val instructions = it["instructions"] as String
                Detail(
                    name = name,
                    image = image,
                    id = id,
                    isAlcoholic = alcoholic,
                    glass = glass,
                    ingredients = ingredients,
                    youtubeLink = null,
                    instructions = instructions,
                    category = category
                )
            }
        return if (customList.isEmpty()) {
            null
        } else {
            customList
        }
    }

    fun generateRandomUserId(): Long {
        return Firebase.auth.currentUser.uid.hashCode().toLong()
    }
}
