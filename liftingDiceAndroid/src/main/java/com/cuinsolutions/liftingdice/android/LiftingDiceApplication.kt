package com.cuinsolutions.liftingdice.android

import android.app.Application
import com.cuinsolutions.liftingdice.android.utils.AndroidModules
import com.cuinsolutions.liftingdice.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class LiftingDiceApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LiftingDiceApplication)
            androidLogger()
            modules(appModule() + AndroidModules().module)
        }
    }
}