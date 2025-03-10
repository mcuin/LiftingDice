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

    @OptIn(ExperimentalObjCName::class)
    @ObjCName("getEquipmentSettings")
    fun wrappedEquipmentSettings() = firebaseDatabase.getEquipmentSettings().wrap()

    @OptIn(ExperimentalObjCName::class)
    @ObjCName("getExercises")
    fun wrappedExercises() = firebaseDatabase.getExercises().wrap()
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}