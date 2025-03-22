package com.cuinsolutions.liftingdice.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cuinsolutions.liftingdice.android.ui.theme.LiftingDiceTheme
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LiftingDiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory (
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        var hasEquipmentSettings = false
        val liftingDiceActivityViewModel: LiftingDiceActivityViewModel by inject()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@LiftingDiceActivity) {}
            MobileAds.setAppMuted(true)
        }
        val splashScreen = installSplashScreen()
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        lifecycleScope.launch {
            liftingDiceActivityViewModel.hasEquipmentSettings.collect {
                hasEquipmentSettings = it
                keepSplashOnScreen = false
                setContent {
                    LiftingDiceTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LiftingDiceAppScreen(hasEquipmentSettings = hasEquipmentSettings)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    LiftingDiceTheme {
        GreetingView("Hello, Android!")
    }
}
