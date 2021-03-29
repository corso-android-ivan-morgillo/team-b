package com.apperol

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.random.Random

interface CustomDrinkRepository {
    suspend fun save(customDrink: Detail): Boolean
    suspend fun delete(id: Long): Boolean
    suspend fun loadAll(): List<Detail>?
    suspend fun loadById(cocktailId: Long): Detail?
}

class CustomDrinkRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authenticationManager: AuthenticationManager,
) : CustomDrinkRepository {
    private val customCollection by lazy {
        firestore.collection("customs")
    }

    override suspend fun save(customDrink: Detail): Boolean {
        if (authenticationManager.isUserLoggedIn()) {
            val id = Random.nextLong().absoluteValue
            val uid = authenticationManager.getUId() ?: return false
            val customMap = hashMapOf(
                "id" to id,
                "name" to customDrink.name,
                "image" to customDrink.image,
                "category" to customDrink.category,
                "glass" to customDrink.glass,
                "alcoholic" to customDrink.isAlcoholic,
                "ingredients" to customDrink.ingredients,
                "instructions" to customDrink.instructions,
                "userid" to uid
            )
            customCollection.document(id.toString()).set(customMap).await()
            Timber.d("INSERITO DETTAGLIO: $customMap")
            return true
        } else {
            return false
        }
    }

    override suspend fun delete(id: Long): Boolean {
        return if (authenticationManager.isUserLoggedIn()) {
            // check if the drink to delete is linked to the user considered
            customCollection.document("$id").delete().await()
            true
        } else {
            false
        }
    }

    override suspend fun loadAll(): List<Detail>? {
        if (authenticationManager.isUserLoggedIn()) {
            val uid = authenticationManager.getUId() ?: return null
            val customList = customCollection
                .whereEqualTo("userid", uid)
                .get()
                .await()
                .documents
                .map {
                    it.toDetail()
                }
            return if (customList.isEmpty()) {
                null
            } else {
                customList
            }
        } else {
            return null
        }
    }

    private fun DocumentSnapshot.toDetail(): Detail {
        val name = this["name"] as String
        val image = this["image"] as String
        val category = this["category"] as String
        val id = this["id"] as Long
        val glass = this["glass"] as String
        val alcoholic = this["alcoholic"] as Boolean
        val ingredients = this["ingredients"] as List<Map<String, String>>
        val instructions = this["instructions"] as String
        return Detail(
            name = name,
            image = image,
            id = id,
            isAlcoholic = alcoholic,
            glass = glass,
            ingredients = ingredients.map {
                Ingredient(
                    name = it["name"]!!,
                    quantity = it["quantity"]!!
                )
            },
            youtubeLink = null,
            instructions = instructions,
            category = category
        )
    }

    override suspend fun loadById(cocktailId: Long): Detail? {
        return if (authenticationManager.isUserLoggedIn()) {
            customCollection.document("$cocktailId").get().await().toDetail()
        } else {
            null
        }
    }
}
