package com.cuinsolutions.liftingdice.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.FirebaseDataSource
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import com.cuinsolutions.liftingdice.MuscleGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.koinInject
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class LiftingDiceWorkoutChoiceScreenViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions): ViewModel() {

    val muscleGroups = firebaseRealtimeDatabaseFunctions.getMuscleGroups().map {
        when {
            it.isNotEmpty() -> LiftingDiceWorkoutChoiceScreenState.Success(it)
            else -> LiftingDiceWorkoutChoiceScreenState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, LiftingDiceWorkoutChoiceScreenState.Loading)

    fun getMuscleGroups() {
        firebaseRealtimeDatabaseFunctions.getMuscleGroups()
    }
}

sealed class LiftingDiceWorkoutChoiceScreenState {
    data object Loading: LiftingDiceWorkoutChoiceScreenState()
    data class Success(val muscleGroups: List<MuscleGroup>): LiftingDiceWorkoutChoiceScreenState()
    data class Error(val message: String): LiftingDiceWorkoutChoiceScreenState()
}