package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiftingDiceAppBar(titleId: Int, canNavBack: Boolean, navigateBack: () -> Unit, canNavigateToEquipmentSettings: Boolean, onNavigateToEquipmentSettings: () -> Unit, modifier: Modifier = Modifier) {

    TopAppBar(modifier = modifier, title = { Text(text = stringResource(id = titleId)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        navigationIcon = {
            if (canNavBack) {
                IconButton(onClick = {
                    navigateBack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            }
        },
        actions = {
            if (canNavigateToEquipmentSettings) {
                IconButton(onClick = {
                    onNavigateToEquipmentSettings()
                }) {
                    Icon(Icons.Default.Settings, contentDescription = "")
                }
            }
        })
}

@Composable
fun LiftingDiceAppScreen(navController: NavHostController = rememberNavController(), hasEquipmentSettings: Boolean) {
    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = if (hasEquipmentSettings) LiftingDiceWorkoutChoiceScreen else LiftingDiceEquipmentOnboarding) {
            composable<LiftingDiceEquipmentOnboarding> {
                LiftingDiceSettingsScreen(modifier = Modifier, canNavigateBack = false, navigateBack = {}, canNavigateToEquipmentSettings = false, onNavigateToEquipmentSettings = {})
            }
            composable<LiftingDiceWorkoutChoiceScreen> {
                LiftingDiceWorkoutChoiceScreen(modifier = Modifier, canNavBack = false, canNavigateToEquipmentSettings = true, navigateBack = {}, onNavigateToEquipmentSettingsScreen = {
                    navController.navigate(LiftingDiceSettingsScreen)
                }, onNavigateToExercisesScreen = { muscleGroupsIds ->
                    navController.navigate(LiftingDiceExercisesScreen(muscleGroupsIds))
                })
            }
            composable<LiftingDiceSettingsScreen> {
                LiftingDiceSettingsScreen(modifier = Modifier, canNavigateBack = true, navigateBack = {
                    navController.navigateUp()
                }, canNavigateToEquipmentSettings = false, onNavigateToEquipmentSettings = {})
            }
            composable<LiftingDiceExercisesScreen> { backStackEntry ->
                val muscleGroupsIds: List<Int> = backStackEntry.arguments?.getIntArray("muscleGroupIds")?.toList() ?: listOf()
                LiftingDiceExercisesScreen(modifier = Modifier, muscleGroupIds = muscleGroupsIds, canGoBack = true, onNavigateBack = { navController.navigateUp() }, canGoToEquipmentSettings = true, onNavigateToEquipmentSettings = { navController.navigate(LiftingDiceSettingsScreen) })
            }
        }
    }
}

@Composable
fun BannerAdview(adId: String) {
    AndroidView(factory = { context ->
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = adId
            loadAd(AdRequest.Builder().build())
        }
    }, modifier = Modifier
        .fillMaxWidth())
}

@Serializable
object LiftingDiceEquipmentOnboarding
@Serializable
object LiftingDiceWorkoutChoiceScreen
@Serializable
object LiftingDiceSettingsScreen
@Serializable
data class LiftingDiceExercisesScreen(val muscleGroupIds: List<Int>)
