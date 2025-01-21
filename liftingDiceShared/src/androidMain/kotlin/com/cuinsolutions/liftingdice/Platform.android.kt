package com.cuinsolutions.liftingdice

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidPlatform : Platform {
    override val name: String
        get() = "android"
    override val isDebug: Boolean
        get() = BuildConfig.DEBUG
}