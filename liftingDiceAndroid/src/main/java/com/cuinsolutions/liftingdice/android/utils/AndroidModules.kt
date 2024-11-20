package com.cuinsolutions.liftingdice.android.utils

import com.cuinsolutions.liftingdice.android.LiftingDiceWorkoutChoiceScreenViewModel
import com.cuinsolutions.liftingdice.android.utils.AndroidModules
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Module
@ComponentScan("com.cuinsolutions.liftingdice.android")
class AndroidModules