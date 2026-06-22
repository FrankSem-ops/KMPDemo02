package com.sem.kmp01.swiftdemo

import androidx.compose.ui.window.ComposeUIViewController
import com.sem.kmp01.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

object SharedUiHost {
    fun makeViewController(): UIViewController = MainViewController()
}
