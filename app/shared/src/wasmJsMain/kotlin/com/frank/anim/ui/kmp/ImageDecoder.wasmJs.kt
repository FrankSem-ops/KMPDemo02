package com.frank.anim.ui.kmp

import androidx.compose.ui.graphics.ImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    throw UnsupportedOperationException("Image decoding is not implemented for Wasm in this demo")
}
