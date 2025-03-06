package com.cuinsolutions.liftingdice.android

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.Exercise
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LiftingDiceExercisesScreenViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions, private val dataStore: DataStore<UserPreferencesOuterClass.UserPreferences>): ViewModel() {

    private val _exercisesUiState = MutableStateFlow<ExercisesLoadState>(ExercisesLoadState.Loading)
    val exercisesUiState = _exercisesUiState.asStateFlow()

    private val dataStoreFlow = dataStore.data
    lateinit var filteredExercises: List<Exercise>

    fun loadExercises(selectedMuscleGroups: List<Int>) {
        firebaseRealtimeDatabaseFunctions.getExercises().combine(dataStoreFlow) { allExercises, equipmentSettings ->
            if (allExercises.isNotEmpty()) {
                if (equipmentSettings.equipmentSettingsIdsList.isNotEmpty()) {
                    val equipmentSettingsIds = equipmentSettings.equipmentSettingsIdsList
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
            while (randomExercises.size < successState.randomExercises.count()) {
                val randomExercise = filteredExercises.random()
                if (reRollExercise != randomExercise && !randomExercises.contains(randomExercise)) {
                    randomExercises.add(index, randomExercise)
                }
            }
            _exercisesUiState .update {
                ExercisesLoadState.Success(successState.allFilteredExercises, successState.diceAmount, randomExercises)
            }
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
            _exercisesUiState.update {
                ExercisesLoadState.Success(successState.allFilteredExercises, successState.diceAmount, randomExercises.toList())
            }
        }
    }
}

sealed class ExercisesLoadState {
    object Loading: ExercisesLoadState()
    data class Success(val allFilteredExercises: List<Exercise>, val diceAmount: Int, val randomExercises: List<Exercise>): ExercisesLoadState()
    data class Error(val message: String): ExercisesLoadState()
}