package com.cuinsolutions.liftingdice

import org.koin.dsl.module

val appModule = module {
    single { FirebaseDataSource() }
    single { FirebaseRealtimeDatabaseFunctions(get()) }
}