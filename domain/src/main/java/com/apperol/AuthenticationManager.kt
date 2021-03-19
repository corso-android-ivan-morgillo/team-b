package com.apperol

import com.apperol.User.LoggedInUser
import com.apperol.User.LoggedOutUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {
    suspend fun getUId(): String?
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getUser(): User
}

class AuthenticationManagerImpl : AuthenticationManager {
    override suspend fun getUId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun getUser(): User {
        return if (isUserLoggedIn()) {
            LoggedInUser(
                email = Firebase.auth.currentUser!!.email,
                name = Firebase.auth.currentUser!!.displayName,
                image = Firebase.auth.currentUser!!.photoUrl?.toString()
            )
        } else {
            LoggedOutUser
        }
    }
}

sealed class User {
    data class LoggedInUser(val email: String?, val name: String?, val image: String?) : User()
    object LoggedOutUser : User()
}
