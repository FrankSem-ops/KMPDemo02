package com.sem.kmp01.swiftdemo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

enum class SyncState(val label: String) {
    IDLE("空闲"),
    CONNECTING("正在连接 Kotlin"),
    COMPLETE("已完成")
}

class SyncSummary(
    val title: String,
    val platformName: String,
    val state: SyncState
) {
    val headline: String
        get() = "$title，当前平台：$platformName"
}

object SwiftExportSamples {
    fun makeSummary(name: String): SyncSummary {
        return SyncSummary(
            title = "你好，$name",
            platformName = "通过 Swift export 接入的 iOS",
            state = SyncState.COMPLETE
        )
    }

    suspend fun loadWelcomeMessage(name: String): String {
        delay(300)
        return "你好，$name，这段文字来自 Kotlin 的 suspend 函数。"
    }

    fun progressUpdates(): Flow<String> = flow {
        emit("已在 Kotlin 侧启动")
        delay(150)
        emit("已从 Flow 映射到 AsyncSequence")
        delay(150)
        emit("已在 Swift 侧完成")
    }
}
