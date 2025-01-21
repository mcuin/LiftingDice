package com.cuinsolutions.liftingdice

import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Single
class FirebaseRealtimeDatabaseFunctions(private val firebaseDatabase: FirebaseDataSource) {

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun getMuscleGroups(): Flow<List<MuscleGroup>> = flow {
        val muscleGroupsReference = firebaseDatabase.getFirebaseDataSource().reference("muscleGroups")
        muscleGroupsReference.valueEvents.collect { snapshot ->
            val muscleGroups = mutableListOf<MuscleGroup>()
            snapshot.children.map { muscleGroup -> muscleGroups.add(muscleGroup.value()) }
            emit(muscleGroups.toList())
        }
    }
}