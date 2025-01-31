package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.cuinsolutions.liftingdice.EquipmentSetting
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceSettingsScreen(modifier: Modifier, navController: NavHostController, liftingDiceSettingsViewModel: LiftingDiceSettingsViewModel = koinViewModel()) {

    val equipmentSettingUiState by liftingDiceSettingsViewModel.equipmentSettingsUiState.collectAsStateWithLifecycle()
    val selectedEquipmentSetting by liftingDiceSettingsViewModel.selectedEquipmentSetting.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier, topBar = {
        LiftingDiceAppBar(titleId = R.string.equipment_settings_title, navController = navController)
    }, bottomBar = {
        BannerAdview()
    }, floatingActionButton = {
        EquipmentSettingFAB(modifier = modifier, navController = navController, viewModel = liftingDiceSettingsViewModel)
    }) { paddingValues ->

        Column(modifier = modifier.padding(paddingValues)) {

            when(val state = equipmentSettingUiState) {
                is LiftingDiceSettingsScreenState.Loading -> {}
                is LiftingDiceSettingsScreenState.Error -> {}
                is LiftingDiceSettingsScreenState.Success -> {
                    LazyColumn(modifier = modifier.fillMaxSize()) {
                        items(state.equipmentSettings) {
                            EquipmentSettingCard(modifier = modifier, equipmentSetting = it, liftingDiceSettingsViewModel, selectedEquipmentSetting)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentSettingCard(modifier: Modifier, equipmentSetting: EquipmentSetting, viewModel: LiftingDiceSettingsViewModel, selectedEquipmentSetting: List<Int>) {
    Card(modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            viewModel.updateEquipmentSetting(equipmentSetting)
        }) {
        Row {
            Text(modifier = modifier.padding(16.dp).align(Alignment.CenterVertically).weight(1f), text = equipmentSetting.name.capitalize(
                Locale.current))
            Checkbox(modifier = modifier.align(Alignment.CenterVertically), checked = selectedEquipmentSetting.contains(equipmentSetting.id), onCheckedChange = {
                viewModel.updateEquipmentSetting(equipmentSetting)
            })
        }
    }
}

@Composable
fun EquipmentSettingFAB(modifier: Modifier, navController: NavHostController, viewModel: LiftingDiceSettingsViewModel) {
    FloatingActionButton(modifier = modifier.padding(16.dp), onClick = {
        viewModel.saveEquipmentSettings()
    }, shape = RoundedCornerShape(16.dp)) {
        Icon(painter = painterResource(R.drawable.ic_save), contentDescription = "")
    }
}