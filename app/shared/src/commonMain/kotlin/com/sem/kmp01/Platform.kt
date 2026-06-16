package com.sem.kmp01

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform