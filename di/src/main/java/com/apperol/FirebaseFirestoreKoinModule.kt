package com.apperol

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val firebaseFirestoreKoinModule = module {

    single<FirebaseFirestore> {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
        Firebase.firestore.firestoreSettings = settings
        Firebase.firestore
    }
}
