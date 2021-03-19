package com.apperol

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

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
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun loadAll(): List<Detail>? {
        TODO("Not yet implemented")
    }

    fun generateRandomUserId(): Int {
        Firebase.auth.currentUser.uid.hashCode()
        return 0
    }
}
