package com.frank.anim.ui.kmp

import android.graphics.BitmapFactory

actual fun getImageSize(imageBytes: ByteArray): Pair<Int, Int>? {
    return try {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
        if (options.outWidth > 0 && options.outHeight > 0) {
            Pair(options.outWidth, options.outHeight)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}


