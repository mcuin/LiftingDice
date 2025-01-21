package com.cuinsolutions.liftingdice

import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import kotlin.experimental.ExperimentalObjCName

class FirebaseHelper: KoinComponent {
    val firebaseDatabase: FirebaseRealtimeDatabaseFunctions by inject()
    @OptIn(ExperimentalObjCName::class)
    @ObjCName("getMuscleGroups")
    fun wrappedMuscleGroups() = firebaseDatabase.getMuscleGroups().wrap()
}

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}