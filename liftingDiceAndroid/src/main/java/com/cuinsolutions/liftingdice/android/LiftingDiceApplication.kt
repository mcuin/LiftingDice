package com.cuinsolutions.liftingdice.android

import android.app.Application
import com.cuinsolutions.liftingdice.android.utils.androidModule
import com.cuinsolutions.liftingdice.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LiftingDiceApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LiftingDiceApplication)
            androidLogger()
            modules(appModule + androidModule)
        }
    }
}