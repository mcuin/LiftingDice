package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.cuinsolutions.liftingdice.MuscleGroup
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceWorkoutChoiceScreen(modifier: Modifier, navController: NavHostController, viewModel: LiftingDiceWorkoutChoiceScreenViewModel = koinViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.getMuscleGroups()
    }

    val selectedMuscleGroups = viewModel.selectedMuscleGroups.collectAsState()

    Scaffold(modifier = modifier.fillMaxSize(), topBar = { LiftingDiceAppBar(R.string.app_name, navController, modifier) },
        bottomBar = { BannerAdview() },
        floatingActionButton = { if (selectedMuscleGroups.value.isNotEmpty()) MuscleGroupsFAB() }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            val uiState = viewModel.muscleGroups.collectAsStateWithLifecycle()

            when (val state = uiState.value) {
                is LiftingDiceWorkoutChoiceScreenState.Error -> {}
                is LiftingDiceWorkoutChoiceScreenState.Loading -> {}
                is LiftingDiceWorkoutChoiceScreenState.Success -> {
                    LazyColumn(modifier = modifier.fillMaxSize()) {
                        items(state.muscleGroups) {
                            MuscleGroupCard(modifier = modifier, muscleGroup = it, viewModel, selectedMuscleGroups)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MuscleGroupCard(modifier: Modifier, muscleGroup: MuscleGroup, viewModel: LiftingDiceWorkoutChoiceScreenViewModel, selectedMuscleGroups: State<List<Int>>) {

    Card(modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            viewModel.updateSelectedMuscleGroup(muscleGroup)
        }) {
        Row {
            Text(modifier = modifier.padding(16.dp).align(Alignment.CenterVertically).weight(1f), text = muscleGroup.name.capitalize(Locale.current))
            Checkbox(modifier = modifier.align(Alignment.CenterVertically), checked = selectedMuscleGroups.value.contains(muscleGroup.id), onCheckedChange = { viewModel.updateSelectedMuscleGroup(muscleGroup) })
        }
    }
}

@Composable
fun MuscleGroupsFAB() {
    FloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(id = R.string.select_muscle_groups_fab_content_description))
    }
}