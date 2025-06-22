package com.cuinsolutions.liftingdice

import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenState
import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiftingDiceWorkoutChoiceScreenViewModelTest: KoinTest {

    val modules = module {
        single { mockk<FirebaseRealtimeDatabaseFunctions>(relaxed = true) }
        single { spyk(LiftingDiceWorkoutChoiceScreenViewModel(get())) }
    }
    private val firebaseRealtimeDatabaseFunctions by inject<FirebaseRealtimeDatabaseFunctions>()
    private val viewModel by inject<LiftingDiceWorkoutChoiceScreenViewModel>()

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
        coEvery { firebaseRealtimeDatabaseFunctions.getMuscleGroups() } returns flowOf(listOf(MuscleGroup("back", 1)))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.muscleGroups.collect {}
        }
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getMuscleGroups() }
        assertEquals(listOf(MuscleGroup("back", 1)), viewModel.muscleGroups.value)
    }

    @Test
    fun `initial state is loading test`() = runTest {
        val latestUIState = viewModel.muscleGroupsState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceWorkoutChoiceScreenState.Loading)
    }

    @Test
    fun `muscle group failed test`() = runTest {
        coEvery { firebaseRealtimeDatabaseFunctions.getMuscleGroups() } returns flowOf(emptyList())
        backgroundScope.launch {
            viewModel.muscleGroups.collect {}
        }
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getMuscleGroups() }
        val latestUIState = viewModel.muscleGroupsState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceWorkoutChoiceScreenState.Loading)
    }

    @Test
    fun `muscle group is updated test`() = runTest {
        viewModel.updateSelectedMuscleGroup(MuscleGroup("chest", 0))
        assertTrue(viewModel.selectedMuscleGroups.value.contains(0))

        viewModel.updateSelectedMuscleGroup(MuscleGroup("chest", 0))
        assertTrue((viewModel.selectedMuscleGroups.value.isEmpty()))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `muscle group ui state test`() = runTest {
        coEvery { firebaseRealtimeDatabaseFunctions.getMuscleGroups() } returns flowOf(listOf(MuscleGroup("back", 1)))
        val initialUIState = viewModel.muscleGroupsState.value
        assertNotNull(initialUIState)
        assertTrue(initialUIState is LiftingDiceWorkoutChoiceScreenState.Loading)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.muscleGroups.collect {
                println(it)
            }
        }
        assertEquals(listOf(MuscleGroup("back", 1)), viewModel.muscleGroups.value)
        coVerify(exactly = 1) { firebaseRealtimeDatabaseFunctions.getMuscleGroups() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.muscleGroupsState.collect {
                println(it)
            }
        }
        val latestUIState = viewModel.muscleGroupsState.value
        assertNotNull(latestUIState)
        assertTrue(latestUIState is LiftingDiceWorkoutChoiceScreenState.Success)
    }
}