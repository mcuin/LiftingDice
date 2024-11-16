package com.cuinsolutions.liftingdice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform