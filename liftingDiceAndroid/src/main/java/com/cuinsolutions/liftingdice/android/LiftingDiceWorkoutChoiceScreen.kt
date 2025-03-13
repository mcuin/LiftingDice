package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuinsolutions.liftingdice.MuscleGroup
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceWorkoutChoiceScreen(modifier: Modifier, canNavBack: Boolean, canNavigateToEquipmentSettings: Boolean, navigateBack: () -> Unit, onNavigateToEquipmentSettingsScreen: () -> Unit, onNavigateToExercisesScreen: (List<Int>) -> Unit, viewModel: LiftingDiceWorkoutChoiceScreenViewModel = koinViewModel()) {

    val uiState = viewModel.muscleGroups.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize(), topBar = { LiftingDiceAppBar(titleId = R.string.app_name, canNavBack = canNavBack, canNavigateToEquipmentSettings = canNavigateToEquipmentSettings, navigateBack = navigateBack, onNavigateToEquipmentSettings = onNavigateToEquipmentSettingsScreen) },
        bottomBar = { BannerAdview() },
        floatingActionButton = { if (uiState.value is LiftingDiceWorkoutChoiceScreenState.Success && (uiState.value as LiftingDiceWorkoutChoiceScreenState.Success).selectedMuscleGroups.isNotEmpty()) MuscleGroupsFAB(onNavigateToExercisesScreen, (uiState.value as LiftingDiceWorkoutChoiceScreenState.Success).selectedMuscleGroups) }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (val state = uiState.value) {
                is LiftingDiceWorkoutChoiceScreenState.Error -> {
                    Text(modifier = modifier.align(Alignment.CenterHorizontally), text = stringResource(id = R.string.select_muscle_groups_error))
                }
                is LiftingDiceWorkoutChoiceScreenState.Loading -> {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(modifier = modifier.align(Alignment.CenterHorizontally),
                                text = stringResource(id = R.string.select_muscle_groups_loading),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                is LiftingDiceWorkoutChoiceScreenState.Success -> {
                    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.fab_bottom_content_padding))) {
                        items(state.muscleGroups) {
                            MuscleGroupCard(modifier = modifier, muscleGroup = it, viewModel, selectedMuscleGroup = state.selectedMuscleGroups.contains(it.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MuscleGroupCard(modifier: Modifier, muscleGroup: MuscleGroup, viewModel: LiftingDiceWorkoutChoiceScreenViewModel, selectedMuscleGroup: Boolean) {

    ElevatedCard(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.standard_card_corner)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.standard_card_elevation)),
        onClick = {
            viewModel.updateSelectedMuscleGroup(muscleGroup)
        }) {
        Row {
            Text(modifier = modifier.padding(dimensionResource(R.dimen.standard_padding)).align(Alignment.CenterVertically).weight(1f), text = muscleGroup.name.capitalize(Locale.current), style = MaterialTheme.typography.titleLarge)
            Checkbox(modifier = modifier.align(Alignment.CenterVertically), checked = selectedMuscleGroup, onCheckedChange = { viewModel.updateSelectedMuscleGroup(muscleGroup) })
        }
    }
}

@Composable
fun MuscleGroupsFAB(onNavigateToExercisesScreen: (List<Int>) -> Unit, selectedMuscleGroups: List<Int>) {
    FloatingActionButton(onClick = {
        onNavigateToExercisesScreen(selectedMuscleGroups)
    }) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(id = R.string.select_muscle_groups_fab_content_description))
    }
}