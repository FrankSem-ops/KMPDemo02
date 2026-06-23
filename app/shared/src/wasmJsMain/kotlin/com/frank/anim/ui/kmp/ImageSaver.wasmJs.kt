package com.frank.anim.ui.kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImageSaver {
    actual suspend fun saveImage(imageBytes: ByteArray, fileName: String): String? = null
}

@Composable
actual fun rememberImageSaver(): ImageSaver = remember { ImageSaver() }
