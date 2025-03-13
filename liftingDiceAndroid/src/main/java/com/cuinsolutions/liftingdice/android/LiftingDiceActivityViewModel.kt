package com.cuinsolutions.liftingdice.android

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LiftingDiceActivityViewModel(private val dataStore: DataStore<UserPreferencesOuterClass.UserPreferences>): ViewModel() {

    val hasEquipmentSettings = dataStore.data.map { preferences ->
        preferences.equipmentSettingsIdsList.isNotEmpty()
    }
}