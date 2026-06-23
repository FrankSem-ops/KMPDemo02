package com.frank.anim.ui.kmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun RemoteImage(
    url: String?,
    cachedData: ByteArray?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val cachedBitmap = remember(cachedData) { runCatching { cachedData?.toImageBitmap() }.getOrNull() }

    when {
        cachedBitmap != null -> Image(
            bitmap = cachedBitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )

        !url.isNullOrBlank() -> PlatformNetworkImage(
            url = url,
            contentDescription = contentDescription,
            modifier = modifier
        )

        else -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "暂无图片",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

expect fun ByteArray.toImageBitmap(): ImageBitmap
