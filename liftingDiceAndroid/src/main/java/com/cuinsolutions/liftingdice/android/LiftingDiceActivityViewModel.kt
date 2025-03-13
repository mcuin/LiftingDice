package com.cuinsolutions.liftingdice.android

import UserPreferencesOuterClass
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map

class LiftingDiceActivityViewModel(private val dataStore: DataStore<UserPreferencesOuterClass.UserPreferences>): ViewModel() {

    val hasEquipmentSettings = dataStore.data.map { preferences ->
        preferences.equipmentSettingsIdsList.isNotEmpty()
    }
}