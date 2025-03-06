package com.cuinsolutions.liftingdice.android.utils

import com.cuinsolutions.liftingdice.android.LiftingDiceExercisesScreenViewModel
import com.cuinsolutions.liftingdice.android.LiftingDiceSettingsViewModel
import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.scope.get
import org.koin.dsl.module

val androidModule = module {
    single { DataStoreModule(androidContext()) }
    single { DataStoreModule(androidContext()).provideProtoDataStore() }
    viewModel { LiftingDiceWorkoutChoiceScreenViewModel(get()) }
    viewModel { LiftingDiceSettingsViewModel(get(), get()) }
    viewModel { LiftingDiceExercisesScreenViewModel(get(), get()) }
}