package com.cuinsolutions.liftingdice.android

import UserPreferencesOuterClass
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.liftingdice.EquipmentSetting
import com.cuinsolutions.liftingdice.FirebaseRealtimeDatabaseFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LiftingDiceSettingsViewModel(private val firebaseRealtimeDatabaseFunctions: FirebaseRealtimeDatabaseFunctions, private val dataStore: DataStore<UserPreferencesOuterClass.UserPreferences>): ViewModel() {

    val selectedEquipmentIdsFlow = MutableStateFlow<List<Int>>(emptyList())
    val equipmentSettings = firebaseRealtimeDatabaseFunctions.getEquipmentSettings().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val dataStoreFlow = dataStore.data.map { preferences ->
        selectedEquipmentIdsFlow.update { equipmentIds ->
            equipmentIds.toMutableList().also {
                it.addAll(preferences.equipmentSettingsIdsList)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesOuterClass.UserPreferences.getDefaultInstance())

    val equipmentSettingsUiState = combine(equipmentSettings, dataStoreFlow, selectedEquipmentIdsFlow) { equipmentSettings, _, selectedEquipmentIds ->
        when {
            equipmentSettings.isNotEmpty() -> LiftingDiceSettingsScreenState.Success(equipmentSettings, selectedEquipmentIds)
            else -> LiftingDiceSettingsScreenState.Error
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LiftingDiceSettingsScreenState.Loading)

    fun updateEquipmentSetting(equipmentSetting: EquipmentSetting) {
        if (selectedEquipmentIdsFlow.value.contains(equipmentSetting.id)) {
            selectedEquipmentIdsFlow.update {
                it.filter { it != equipmentSetting.id }
            }
        } else {
            selectedEquipmentIdsFlow.update {
                it.plus(equipmentSetting.id)
            }
        }
    }

    fun saveEquipmentSettings() {
        viewModelScope.launch {
            dataStore.updateData {
                it.toBuilder().clearEquipmentSettingsIds().addAllEquipmentSettingsIds(selectedEquipmentIdsFlow.value).build()
            }
        }
    }
}

sealed class LiftingDiceSettingsScreenState {
    data object Loading: LiftingDiceSettingsScreenState()
    data class Success(val equipmentSettings: List<EquipmentSetting>, val selectedEquipmentIds: List<Int>): LiftingDiceSettingsScreenState()
    data object Error: LiftingDiceSettingsScreenState()
}