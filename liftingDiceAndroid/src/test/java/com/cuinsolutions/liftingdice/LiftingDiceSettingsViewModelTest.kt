package com.cuinsolutions.liftingdice

import androidx.datastore.core.DataStore
import com.cuinsolutions.liftingdice.android.LiftingDiceSettingsScreenState
import com.cuinsolutions.liftingdice.android.LiftingDiceSettingsViewModel
import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenState
import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertNotNull
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiftingDiceSettingsViewModelTest: KoinTest {

    val modules = module {
        single { mockk<FirebaseRealtimeDatabaseFunctions>(relaxed = true) }
        single { mockk<DataStore<UserPreferencesOuterClass.UserPreferences>>(relaxed = true) }
        single { spyk(LiftingDiceSettingsViewModel(get() ,get())) }
    }
    private val firebaseRealtimeDatabaseFunctions by inject<FirebaseRealtimeDatabaseFunctions>()
    private val viewModel by inject<LiftingDiceSettingsViewModel>()
    private val dataStore by inject<DataStore<UserPreferencesOuterClass.UserPreferences>>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(modules)
    }

    @get:Rule
    val mockkProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `muscle groups not empty test`() = runTest {
        coEvery { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() } returns flowOf(listOf(EquipmentSetting("no equipment", 0)))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.equipmentSettings.collect {}
        }
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() }
        assertEquals(listOf(EquipmentSetting("no equipment", 0)), viewModel.equipmentSettings.value)
    }

    @Test
    fun `initial state is loading test`() = runTest {
        val latestUIState = viewModel.equipmentSettingsUiState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceSettingsScreenState.Loading)
    }

    @Test
    fun `muscle group failed test`() = runTest {
        coEvery { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() } returns flowOf(emptyList())
        backgroundScope.launch {
            viewModel.equipmentSettings.collect {}
        }
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() }
        val latestUIState = viewModel.equipmentSettingsUiState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceSettingsScreenState.Loading)
    }

    @Test
    fun `muscle group is updated test`() = runTest {
        viewModel.updateEquipmentSetting(EquipmentSetting("dumbbells", 1))
        assertTrue(viewModel.selectedEquipmentIdsFlow.value.contains(1))

        viewModel.updateEquipmentSetting(EquipmentSetting("dumbbells", 1))
        assertTrue((viewModel.selectedEquipmentIdsFlow.value.isEmpty()))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `muscle group ui state test`() = runTest {
        coEvery { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() } returns flowOf(listOf(EquipmentSetting("no equipment", 0)))
        val initialUIState = viewModel.equipmentSettingsUiState.value
        assertNotNull(initialUIState)
        assertTrue(initialUIState is LiftingDiceSettingsScreenState.Loading)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.equipmentSettings.collect {
                println(it)
            }
        }
        assertEquals(listOf(EquipmentSetting("no equipment", 0)), viewModel.equipmentSettings.value)
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getEquipmentSettings() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.equipmentSettingsUiState.collect {
                println(it)
            }
        }
        val latestUIState = viewModel.equipmentSettingsUiState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceSettingsScreenState.Success)
    }

    @Test
    fun `data store test`() = runTest {
        viewModel.saveEquipmentSettings()
        coVerify(exactly = 1) { dataStore.updateData(any()) }
    }
}