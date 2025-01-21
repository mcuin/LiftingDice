package com.cuinsolutions.liftingdice

interface Platform {
    val name: String
    val isDebug: Boolean
}

expect fun getPlatform(): Platform