package com.sem.kmp01

class WasmPlatform: Platform {
    override val name: String = "基于 Kotlin/Wasm 的网页"
}

actual fun getPlatform(): Platform = WasmPlatform()
