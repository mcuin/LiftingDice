package com.cuinsolutions.liftingdice

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform.isDebugBinary

actual fun getPlatform(): Platform = IOSPlatform()

class IOSPlatform: Platform {
    override val name: String
        get() = "ios"
    @OptIn(ExperimentalNativeApi::class)
    override val isDebug: Boolean
        get() = isDebugBinary
}