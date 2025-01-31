package com.cuinsolutions.liftingdice.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.EquipmentSetting
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LiftingDiceSettingsViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions): ViewModel() {

    val equipmentSettingsUiState = firebaseRealtimeDatabaseFunctions.getEquipmentSettings().map { equipmentSettings ->
        when {
            equipmentSettings.isNotEmpty() -> LiftingDiceSettingsScreenState.Success(equipmentSettings)
            else -> LiftingDiceSettingsScreenState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LiftingDiceSettingsScreenState.Loading)

    val selectedEquipmentSetting = MutableStateFlow<List<Int>>(emptyList())

    fun updateEquipmentSetting(equipmentSetting: EquipmentSetting) {
        if (selectedEquipmentSetting.value.contains(equipmentSetting.id)) {
            selectedEquipmentSetting.value = selectedEquipmentSetting.value.filter { it != equipmentSetting.id }
        } else {
            selectedEquipmentSetting.value = selectedEquipmentSetting.value.plus(equipmentSetting.id)
        }
    }

    fun saveEquipmentSettings() {

    }
}

sealed class LiftingDiceSettingsScreenState {
    data object Loading: LiftingDiceSettingsScreenState()
    data class Success(val equipmentSettings: List<EquipmentSetting>): LiftingDiceSettingsScreenState()
    data class Error(val message: String): LiftingDiceSettingsScreenState()
}