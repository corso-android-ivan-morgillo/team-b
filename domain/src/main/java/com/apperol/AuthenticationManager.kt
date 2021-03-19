package com.apperol

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {
    suspend fun getUId(): String?
    suspend fun isUserLoggedIn(): Boolean
}

class AuthenticationManagerImpl : AuthenticationManager {
    override suspend fun getUId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}
