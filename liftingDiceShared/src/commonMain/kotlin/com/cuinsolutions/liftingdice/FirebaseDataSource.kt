package com.cuinsolutions.liftingdice

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class FirebaseDataSource {
    fun getFirebaseDataSource(): FirebaseDatabase {
        val instance = /*if (BuildConfig.DEBUG) {*/
            Firebase.database("http://10.0.2.2:9000/?ns=liftingdice")
        //} else {
        //FirebaseDatabase.getInstance()
        //}
        instance.setPersistenceEnabled(true)
        return instance
    }
}