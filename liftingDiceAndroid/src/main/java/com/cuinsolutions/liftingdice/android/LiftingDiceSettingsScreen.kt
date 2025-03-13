package com.cuinsolutions.liftingdice.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuinsolutions.liftingdice.EquipmentSetting
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiftingDiceSettingsScreen(modifier: Modifier, canNavigateBack: Boolean, navigateBack: () -> Unit, canNavigateToEquipmentSettings: Boolean, onNavigateToEquipmentSettings: () -> Unit, liftingDiceSettingsViewModel: LiftingDiceSettingsViewModel = koinViewModel()) {

    val equipmentSettingUiState by liftingDiceSettingsViewModel.equipmentSettingsUiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier, topBar = {
        LiftingDiceAppBar(titleId = R.string.equipment_settings_title, canNavBack = canNavigateBack, navigateBack = navigateBack, canNavigateToEquipmentSettings = false, onNavigateToEquipmentSettings = onNavigateToEquipmentSettings)
    }, bottomBar = {
        BannerAdview()
    }, floatingActionButton = {
        if ((equipmentSettingUiState is LiftingDiceSettingsScreenState.Success) && (equipmentSettingUiState as LiftingDiceSettingsScreenState.Success).selectedEquipmentIds.isNotEmpty())
            EquipmentSettingFAB(modifier = modifier, navigateBack = navigateBack, viewModel = liftingDiceSettingsViewModel)
    }) { paddingValues ->

        Column(modifier = modifier.padding(paddingValues)) {

            when(val state = equipmentSettingUiState) {
                is LiftingDiceSettingsScreenState.Loading -> {
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(
                                modifier = modifier.fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.standard_padding)),
                                text = stringResource(R.string.equipment_settings_loading),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is LiftingDiceSettingsScreenState.Error -> {
                    Text(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)), text = stringResource(R.string.equipment_settings_error), style = MaterialTheme.typography.titleLarge)
                }
                is LiftingDiceSettingsScreenState.Success -> {
                    if (!canNavigateBack) {
                        Text(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)), text = stringResource(R.string.equipment_setting_onboarding_description), style = MaterialTheme.typography.titleLarge)
                    }
                    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.fab_bottom_content_padding))) {
                        itemsIndexed(state.equipmentSettings) { index, equipmentSetting ->
                            EquipmentSettingCard(modifier = modifier, equipmentSetting = equipmentSetting, viewModel = liftingDiceSettingsViewModel, selectedEquipmentSetting = state.selectedEquipmentIds.contains(equipmentSetting.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentSettingCard(modifier: Modifier, equipmentSetting: EquipmentSetting, viewModel: LiftingDiceSettingsViewModel, selectedEquipmentSetting: Boolean) {
    ElevatedCard(modifier = modifier.fillMaxWidth().padding(dimensionResource(R.dimen.standard_padding)),
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.standard_card_elevation)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.standard_card_corner)),
        onClick = {
            viewModel.updateEquipmentSetting(equipmentSetting)
        }) {
        Row {
            Text(modifier = modifier.padding(16.dp).align(Alignment.CenterVertically).weight(1f), text = equipmentSetting.name.capitalize(
                Locale.current), style = MaterialTheme.typography.titleLarge)
            Checkbox(modifier = modifier.align(Alignment.CenterVertically), checked = selectedEquipmentSetting, onCheckedChange = {
                viewModel.updateEquipmentSetting(equipmentSetting)
            })
        }
    }
}

@Composable
fun EquipmentSettingFAB(modifier: Modifier, navigateBack: () -> Unit, viewModel: LiftingDiceSettingsViewModel) {
    FloatingActionButton(modifier = modifier.padding(16.dp), onClick = {
        viewModel.saveEquipmentSettings()
        navigateBack()
    }, shape = RoundedCornerShape(16.dp)) {
        Icon(painter = painterResource(R.drawable.ic_save), contentDescription = "")
    }
}