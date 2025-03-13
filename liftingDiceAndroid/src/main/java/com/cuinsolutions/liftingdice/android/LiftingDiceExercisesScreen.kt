package com.cuinsolutions.liftingdice.android

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuinsolutions.liftingdice.Exercise
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceExercisesScreen(modifier: Modifier, muscleGroupIds: List<Int>, canGoBack: Boolean, canGoToEquipmentSettings: Boolean, onNavigateBack: () -> Unit, onNavigateToEquipmentSettings: () -> Unit, liftingDiceExercisesScreenViewModel: LiftingDiceExercisesScreenViewModel = koinViewModel()) {

    val exerciseState = liftingDiceExercisesScreenViewModel.exercisesUiState.collectAsStateWithLifecycle()
    val context = LocalActivity.current as Activity
    val rewardedAd = remember { ExercisesRerollRewardedAd(liftingDiceExercisesScreenViewModel) }
    var showRerollAlert by remember { mutableStateOf(Pair(false, -1)) }

    LaunchedEffect(key1 = Unit) {
        liftingDiceExercisesScreenViewModel.loadExercises(muscleGroupIds)
    }

    Scaffold(modifier = modifier, topBar = { LiftingDiceAppBar(titleId = R.string.exercises_roll_title, canNavBack = canGoBack, canNavigateToEquipmentSettings = canGoToEquipmentSettings, navigateBack = onNavigateBack, onNavigateToEquipmentSettings = onNavigateToEquipmentSettings) },
        floatingActionButton = { if (liftingDiceExercisesScreenViewModel.filteredExercises.count() > 6) ExercisesReRollAllFab(modifier = modifier, viewModel = liftingDiceExercisesScreenViewModel, showRerollAlert = { reoll, index -> showRerollAlert = Pair(reoll, index) }) },
        bottomBar = { BannerAdview() }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = exerciseState.value) {
                is ExercisesLoadState.Error -> {
                    Text(
                        modifier = modifier.padding(padding),
                        text = stringResource(R.string.exercises_error)
                    )
                }

                ExercisesLoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column {
                            CircularProgressIndicator()
                            Text(
                                modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                                text = stringResource(R.string.exercises_loading)
                            )
                        }
                    }
                }

                is ExercisesLoadState.Success -> {

                    rewardedAd.loadRewardedVideoAd(context)
                    Text(
                        modifier = modifier.fillMaxWidth()
                            .padding(dimensionResource(R.dimen.standard_padding)),
                        text = stringResource(R.string.exercises_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LazyVerticalGrid(
                        modifier = modifier.fillMaxWidth(),
                        columns = GridCells.Fixed(2)
                    ) {
                        itemsIndexed(state.randomExercises) { index, exercise ->
                            ExerciseCard(
                                modifier = modifier,
                                exercise = exercise,
                                index = index,
                                viewModel = liftingDiceExercisesScreenViewModel,
                                showRerollAlert = { reroll, index ->
                                    showRerollAlert = Pair(reroll, index)
                                })
                        }
                    }
                    Text(
                        modifier = modifier.fillMaxWidth()
                            .padding(dimensionResource(R.dimen.standard_padding)),
                        text = stringResource(R.string.exercises_warning),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (showRerollAlert.first) {
                        RerollAlert(reRollAlertTitle = context.getString(R.string.exercise_out_of_rerolls_title),
                            context.getString(R.string.exercise_out_of_rerolls_description),
                            onDismiss = {
                                showRerollAlert = showRerollAlert.copy(first = false)
                            },
                            onConfirm = {
                                showRerollAlert = showRerollAlert.copy(first = false)
                                rewardedAd.showRewardedAd(context, showRerollAlert.second)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun ExercisesReRollAllFab(modifier: Modifier, viewModel: LiftingDiceExercisesScreenViewModel, showRerollAlert: (Boolean, Int) -> Unit) {
    ExtendedFloatingActionButton(modifier = modifier, onClick = {
        if (viewModel.currentRerolls <= 0) {
            showRerollAlert(true, -1)
        } else {
            viewModel.reRollAll()
        }
    }) {
        Text(text = stringResource(R.string.exercises_reroll_all))
    }
}

@Composable
fun ExerciseCard(modifier: Modifier, exercise: Exercise, index: Int, viewModel: LiftingDiceExercisesScreenViewModel, showRerollAlert: (Boolean, Int) -> Unit) {

    var cardText by remember { mutableStateOf("") }

    Card(modifier = modifier.fillMaxWidth().padding(16.dp).height(dimensionResource(R.dimen.exercise_card_height)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.standard_card_corner))) {
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
            Column {
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
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = stringResource(R.string.exercise_info_content_description)
                    )
                }
                if(viewModel.filteredExercises.count() > 6) {
                    IconButton(onClick = {
                        if (viewModel.currentRerolls <= 0) {
                            showRerollAlert(true, index)
                        } else {
                            viewModel.reRollItem(index)
                        }
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_dice), contentDescription = stringResource(R.string.exercise_dice_content_description))
                    }
                }
            }
        }
    }
}

@Composable
fun RerollAlert(reRollAlertTitle: String, reRollAlertDescription: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(title = { Text(text = reRollAlertTitle) },
        text = { Text(text = reRollAlertDescription) },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(text = stringResource(R.string.exercise_out_of_rerolls_watch_ad))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        })
}

class ExercisesRerollRewardedAd(val liftingDiceExercisesScreenViewModel: LiftingDiceExercisesScreenViewModel) {

    private var rewardedAd: RewardedAd? = null

    fun loadRewardedVideoAd(activity: Activity) {

        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            activity,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedAd = null
                    println("Ad failed to load")
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    rewardedAd = ad
                    println("Ad loaded")
                }
            })
    }

    fun showRewardedAd(activity: Activity, index: Int) {
        if (rewardedAd == null) {
            println("Ad null")
            return
        }
        rewardedAd?.show(activity) { rewardItem ->
            liftingDiceExercisesScreenViewModel.resetRerolls(rewardItem.amount)
        }

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                super.onAdClicked()
                println("Ad clicked")
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                if (index != -1) {
                    liftingDiceExercisesScreenViewModel.reRollItem(index)
                } else {
                    liftingDiceExercisesScreenViewModel.reRollAll()
                }
                println("Ad dismissed")
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                println("Ad failed to show")
                rewardedAd = null
            }

            override fun onAdImpression() {
                super.onAdImpression()
                println("Ad impression")
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                println("Ad showed")
            }
        }
    }
}