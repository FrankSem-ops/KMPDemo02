package com.frank.anim.ui.kmp

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
        ?: throw IllegalArgumentException("Failed to decode image from byte array")
    return bitmap.asImageBitmap()
}