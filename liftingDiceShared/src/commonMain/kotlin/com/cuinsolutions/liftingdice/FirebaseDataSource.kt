package com.cuinsolutions.liftingdice

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database

class FirebaseDataSource {
    val firebaseDatabase: FirebaseDatabase = when {
            getPlatform().isDebug && getPlatform().name == "android" -> Firebase.database("http://10.0.2.2:9000/?ns=liftingdice")
            getPlatform().isDebug && getPlatform().name == "ios" -> Firebase.database("http://127.0.0.1:9000/?ns=liftingdice")
            else -> Firebase.database
        }.apply {
            setPersistenceEnabled(true)
        }
}