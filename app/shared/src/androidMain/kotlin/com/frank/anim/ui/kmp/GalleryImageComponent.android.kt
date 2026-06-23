package com.frank.anim.ui.kmp

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android平台的图片加载器
 */
@Composable
actual fun PlatformImageLoader(
    imageUrl: String,
    modifier: Modifier,
    contentScale: ContentScale,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmapState by remember(imageUrl) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var failed by remember(imageUrl) { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        bitmapState = null
        failed = false
        bitmapState = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(imageUrl))?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }.getOrNull()
        }
        failed = bitmapState == null
    }

    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(Color(0xFFF0F0F0))
    ) {
        val bitmap = bitmapState
        when {
            bitmap != null -> Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Gallery image",
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )

            failed -> Text(
                text = "图片加载失败",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
