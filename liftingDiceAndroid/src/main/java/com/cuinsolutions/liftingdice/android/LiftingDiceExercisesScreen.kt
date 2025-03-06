package com.cuinsolutions.liftingdice.android

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuinsolutions.liftingdice.Exercise
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceExercisesScreen(modifier: Modifier, muscleGroupIds: List<Int>, canGoBack: Boolean, canGoToEquipmentSettings: Boolean, onNavigateBack: () -> Unit, onNavigateToEquipmentSettings: () -> Unit, liftingDiceExercisesScreenViewModel: LiftingDiceExercisesScreenViewModel = koinViewModel()) {

    val exerciseState = liftingDiceExercisesScreenViewModel.exercisesUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        liftingDiceExercisesScreenViewModel.loadExercises(muscleGroupIds)
    }

    Scaffold(modifier = modifier, topBar = { LiftingDiceAppBar(titleId = R.string.exercises_roll_title, canNavBack = canGoBack, canNavigateToEquipmentSettings = canGoToEquipmentSettings, navigateBack = onNavigateBack, onNavigateToEquipmentSettings = onNavigateToEquipmentSettings) },
        floatingActionButton = { ExercisesReRollAllFab(modifier = modifier, viewModel = liftingDiceExercisesScreenViewModel) },
        bottomBar = { BannerAdview() }) { padding ->

        when(val state = exerciseState.value) {
            is ExercisesLoadState.Error -> {}
            ExercisesLoadState.Loading -> {}
            is ExercisesLoadState.Success -> {
                Column(modifier = Modifier.padding(padding)) {
                    Text(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)), text = stringResource(R.string.exercises_description),
                        style = MaterialTheme.typography.bodyMedium)
                    LazyVerticalGrid(modifier = modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
                        itemsIndexed(state.randomExercises) { index, exercise ->
                            ExerciseCard(modifier = modifier, exercise = exercise, index = index, viewModel = liftingDiceExercisesScreenViewModel)
                        }
                    }
                    Text(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)), text = stringResource(R.string.exercises_warning),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ExercisesReRollAllFab(modifier: Modifier, viewModel: LiftingDiceExercisesScreenViewModel) {
    ExtendedFloatingActionButton(modifier = modifier, onClick = {
        viewModel.reRollAll()
    }) {
        Text(text = stringResource(R.string.exercises_reroll_all))
    }
}

@Composable
fun ExerciseCard(modifier: Modifier, exercise: Exercise, index: Int, viewModel: LiftingDiceExercisesScreenViewModel) {

    var cardText by remember { mutableStateOf("") }

    Card(modifier = modifier.fillMaxWidth().padding(16.dp).height(dimensionResource(R.dimen.exercise_card_height)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.standard_card_corner)),
        onClick = {
        viewModel.reRollItem(index)
    }) {
        LaunchedEffect(exercise) {
            repeat(5) {
                cardText = viewModel.filteredExercises.random().name.capitalize(Locale.current)
                delay(250)
            }
            cardText = exercise.name.capitalize(Locale.current)
        }
        Row {
            val context = LocalContext.current
            Text(modifier = modifier.weight(1f).padding(dimensionResource(R.dimen.small_padding)), text = cardText,
                style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, exercise.name)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    val googleIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.google.com/search?q=${exercise.name}")
                    }
                    context.startActivity(googleIntent)
                }
            }) {
                Icon(painter = painterResource(R.drawable.ic_info), contentDescription = stringResource(R.string.exercise_info_content_description))
            }
        }
    }
}