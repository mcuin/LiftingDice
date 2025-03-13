package com.cuinsolutions.liftingdice.android

import UserPreferencesOuterClass
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.Exercise
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LiftingDiceExercisesScreenViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions, private val dataStore: DataStore<UserPreferencesOuterClass.UserPreferences>): ViewModel() {

    private val _exercisesUiState = MutableStateFlow<ExercisesLoadState>(ExercisesLoadState.Loading)
    val exercisesUiState = _exercisesUiState.asStateFlow()

    var currentRerolls = 0
    private val selectedEquipmentIds = MutableStateFlow<List<Int>>(emptyList())
    private val dataStoreFlow = dataStore.data.map { dataStore ->
        selectedEquipmentIds.update {
            dataStore.equipmentSettingsIdsList
        }
        currentRerolls = dataStore.rerolls
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Unit)
    var filteredExercises: List<Exercise> = emptyList()

    fun loadExercises(selectedMuscleGroups: List<Int>) {
        firebaseRealtimeDatabaseFunctions.getExercises().combine(selectedEquipmentIds) { allExercises, equipmentIds ->
            if (allExercises.isNotEmpty()) {
                if (equipmentIds.isNotEmpty()) {
                    val equipmentSettingsIds = equipmentIds
                    filteredExercises = allExercises.filter { exercise ->
                        selectedMuscleGroups.any {selectedMuscleGroup ->
                            selectedMuscleGroup in exercise.muscleGroupIds
                        }
                    }.filter { exercise ->
                        exercise.equipmentIds.any { equipmentId -> equipmentId in equipmentSettingsIds }
                    }
                    val diceAmount = when {
                        filteredExercises.count() >= 6 && selectedMuscleGroups.count() <= 6 -> 6
                        filteredExercises.count() < 6 -> filteredExercises.count()
                        selectedMuscleGroups.count() > 6 && filteredExercises.count() >= selectedMuscleGroups.count() -> selectedMuscleGroups.count()
                        else -> 6
                    }
                    val randomExercises = mutableSetOf<Exercise>()
                    while (randomExercises.size < diceAmount) {
                        val randomExercise = filteredExercises.random()
                        randomExercises.add(randomExercise)
                    }
                    _exercisesUiState.update {
                        ExercisesLoadState.Success(filteredExercises, diceAmount, randomExercises.toList())
                    }
                } else {
                    filteredExercises = allExercises.filter { exercises -> exercises.muscleGroupIds.any { muscleGroup -> selectedMuscleGroups.contains(muscleGroup) }}.filter { exercise -> exercise.equipmentIds.contains(0) }
                    val diceAmount = when {
                        filteredExercises.count() >= 6 && selectedMuscleGroups.count() <= 6 -> 6
                        filteredExercises.count() < 6 -> filteredExercises.count()
                        selectedMuscleGroups.count() > 6 && filteredExercises.count() >= selectedMuscleGroups.count() -> selectedMuscleGroups.count()
                        else -> 6
                    }
                    val randomExercises = mutableSetOf<Exercise>()
                    while (randomExercises.size < diceAmount) {
                        val randomExercise = filteredExercises.random()
                        randomExercises.add(randomExercise)
                    }
                    _exercisesUiState.update {
                        ExercisesLoadState.Success(filteredExercises, diceAmount, randomExercises.toList())
                    }
                }
            } else {
                _exercisesUiState.update {
                    ExercisesLoadState.Error("No allExercises found")
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ExercisesLoadState.Loading)
    }

    fun reRollItem(index: Int) {
        if (exercisesUiState.value is ExercisesLoadState.Success) {
            val successState = exercisesUiState.value as ExercisesLoadState.Success
            val reRollExercise = successState.randomExercises[index]
            val randomExercises = successState.randomExercises.toMutableList()
            randomExercises.remove(reRollExercise)
            while (randomExercises.count() < successState.randomExercises.count()) {
                val randomExercise = filteredExercises.random()
                if (reRollExercise != randomExercise && !randomExercises.contains(randomExercise)) {
                    randomExercises.add(index, randomExercise)
                }
            }
            _exercisesUiState.update { currentState ->
                (currentState as ExercisesLoadState.Success).copy(
                    randomExercises = randomExercises
                )
            }
            updateRerolls()
        }
    }

    fun reRollAll() {
        if (exercisesUiState.value is ExercisesLoadState.Success) {
            val successState = exercisesUiState.value as ExercisesLoadState.Success
            val randomExercises = mutableSetOf<Exercise>()
            while (randomExercises.size < successState.diceAmount) {
                val randomExercise = filteredExercises.random()
                randomExercises.add(randomExercise)
            }
            _exercisesUiState.update { currentState ->
                (currentState as ExercisesLoadState.Success).copy(
                    randomExercises = randomExercises.toList()
                )
            }
            updateRerolls()
        }
    }

    private fun updateRerolls() {
        viewModelScope.launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.toBuilder().setRerolls(currentRerolls - 1).build()
            }
        }
    }

    fun resetRerolls(rewardedRolls: Int) {
        viewModelScope.launch {
            dataStore.updateData { currentPreferences ->
                currentPreferences.toBuilder().setRerolls(rewardedRolls).build()
            }
        }
    }
}

sealed class ExercisesLoadState {
    object Loading: ExercisesLoadState()
    data class Success(val allFilteredExercises: List<Exercise>, val diceAmount: Int, val randomExercises: List<Exercise>): ExercisesLoadState()
    data class Error(val message: String): ExercisesLoadState()
}