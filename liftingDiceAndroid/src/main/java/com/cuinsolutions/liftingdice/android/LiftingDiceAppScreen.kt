package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiftingDiceAppBar(titleId: Int, navController: NavHostController, modifier: Modifier = Modifier) {

    TopAppBar(title = { Text(text = stringResource(id = titleId)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier, actions = {
            IconButton(onClick = { /*navController.navigate()*/ }) {
                Icon(Icons.Default.Settings, contentDescription = "")
            }
        })
}

@Composable
fun LiftingDiceAppScreen(navController: NavHostController = rememberNavController()) {
    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = LiftingDiceScreens.LiftingDiceWorkoutChoiceScreen().name) {
            composable(LiftingDiceScreens.LiftingDiceWorkoutChoiceScreen().name) {
                LiftingDiceWorkoutChoiceScreen(modifier = Modifier, navController = navController)
            }
        }
    }
}

@Composable
fun BannerAdview() {
    AndroidView(factory = { context ->
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            loadAd(AdRequest.Builder().build())
        }
    }, modifier = Modifier
        .fillMaxWidth())
}

sealed class LiftingDiceScreens() {
    data class LiftingDiceWorkoutChoiceScreen(val name: String = "LiftingDiceWorkoutChoiceScreen") : LiftingDiceScreens()
}