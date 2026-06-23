package com.frank.anim.ui.kmp

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class ImageSaver(private val context: Context) {
    actual suspend fun saveImage(imageBytes: ByteArray, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 使用外部存储的 Pictures 目录
                val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: context.filesDir
                
                val file = File(picturesDir, "$fileName.jpg")
                
                FileOutputStream(file).use { fos ->
                    fos.write(imageBytes)
                }
                
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

@androidx.compose.runtime.Composable
actual fun rememberImageSaver(): ImageSaver {
    val context = LocalContext.current
    return androidx.compose.runtime.remember { ImageSaver(context) }
}

