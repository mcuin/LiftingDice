package com.cuinsolutions.liftingdice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

class FirebaseRealtimeDatabaseFunctions (private val firebaseDataSource: FirebaseDataSource) {

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun getMuscleGroups(): Flow<List<MuscleGroup>> = flow {
        val muscleGroupsReference = firebaseDataSource.firebaseDatabase.reference("muscleGroups")
        muscleGroupsReference.valueEvents.collect { snapshot ->
            val muscleGroups = mutableListOf<MuscleGroup>()
            snapshot.children.map { muscleGroup -> muscleGroups.add(muscleGroup.value()) }
            emit(muscleGroups.toList())
        }
    }

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun getEquipmentSettings(): Flow<List<EquipmentSetting>> = flow {
        val equipmentSettingsReference = firebaseDataSource.firebaseDatabase.reference("equipmentSettings")
        equipmentSettingsReference.valueEvents.collect { snapshot ->
            val equipmentSettings = mutableListOf<EquipmentSetting>()
            snapshot.children.map { equipmentSetting -> equipmentSettings.add(equipmentSetting.value()) }
            emit(equipmentSettings.toList())
        }
    }

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    fun getExercises(): Flow<List<Exercise>> = flow {
        val exercisesReference = firebaseDataSource.firebaseDatabase.reference("exercises")
        exercisesReference.valueEvents.collect { snapshot ->
            val exercises = mutableListOf<Exercise>()
            snapshot.children.map { exercise -> exercises.add(exercise.value()) }
            emit(exercises.toList())
        }
    }
}