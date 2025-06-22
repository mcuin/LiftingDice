package com.cuinsolutions.liftingdice.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import com.cuinsolutions.liftingdice.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LiftingDiceWorkoutChoiceScreenViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions): ViewModel() {

    val selectedMuscleGroups = MutableStateFlow<List<Int>>(emptyList())
    val muscleGroups = firebaseRealtimeDatabaseFunctions.getMuscleGroups().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val muscleGroupsState = combine(muscleGroups, selectedMuscleGroups) { muscleGroups, selectedMuscleGroups ->
        when {
            muscleGroups.isNotEmpty() -> LiftingDiceWorkoutChoiceScreenState.Success(muscleGroups, selectedMuscleGroups)
            else -> LiftingDiceWorkoutChoiceScreenState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, LiftingDiceWorkoutChoiceScreenState.Loading)

    fun updateSelectedMuscleGroup(muscleGroup: MuscleGroup) {
        if (selectedMuscleGroups.value.contains(muscleGroup.id)) {
            selectedMuscleGroups.value = selectedMuscleGroups.value.filter { it != muscleGroup.id }
        } else {
            selectedMuscleGroups.value = selectedMuscleGroups.value.plus(muscleGroup.id)
        }
    }
}

sealed class LiftingDiceWorkoutChoiceScreenState {
    data object Loading: LiftingDiceWorkoutChoiceScreenState()
    data class Success(val muscleGroups: List<MuscleGroup>, val selectedMuscleGroups: List<Int> = emptyList()): LiftingDiceWorkoutChoiceScreenState()
    data class Error(val message: String): LiftingDiceWorkoutChoiceScreenState()
}