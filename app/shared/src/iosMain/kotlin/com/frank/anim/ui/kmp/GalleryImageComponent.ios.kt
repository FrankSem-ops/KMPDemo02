package com.frank.anim.ui.kmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo
import platform.Photos.PHAsset
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestID
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeHighQualityFormat
import platform.Photos.PHImageRequestOptionsResizeModeFast
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.useContents
import platform.posix.memcpy

/**
 * iOS平台的图片加载器
 */
@Composable
actual fun PlatformImageLoader(
    imageUrl: String,
    modifier: Modifier,
    contentScale: ContentScale,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        isLoading = true
        hasError = false
        bitmap = null

        val imageBitmap = withContext(Dispatchers.Default) {
            runCatching { loadImageBitmap(imageUrl) }.getOrNull()
        }

        if (imageBitmap != null) {
            bitmap = imageBitmap
        } else {
            hasError = true
        }
        isLoading = false
    }

    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            bitmap != null -> {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = "Gallery image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }

            hasError -> {
                Text(
                    text = "无法加载图片",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun loadImageBitmap(localIdentifier: String): ImageBitmap? = suspendCancellableCoroutine { cont ->
    val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(listOf(localIdentifier), null)
    val asset = if (fetchResult.count.toInt() > 0) {
        fetchResult.objectAtIndex(0uL) as? PHAsset
    } else null
    if (asset == null || asset.mediaType != PHAssetMediaTypeImage) {
        cont.resume(null)
        return@suspendCancellableCoroutine
    }

    val options = PHImageRequestOptions().apply {
        deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat
        resizeMode = PHImageRequestOptionsResizeModeFast
        synchronous = false
        networkAccessAllowed = true
    }

    val manager = PHImageManager.defaultManager()
    val requestId: PHImageRequestID = if (isAtLeastIOS13()) {
        manager.requestImageDataAndOrientationForAsset(asset, options) { data, _, _, _ ->
            dispatch_async(dispatch_get_main_queue()) {
                if (cont.isActive) {
                    cont.resume(data?.toByteArraySafe()?.toImageBitmapOrNull())
                }
            }
        }
    } else {
        manager.requestImageDataForAsset(asset, options) { data, _, _, _ ->
            dispatch_async(dispatch_get_main_queue()) {
                if (cont.isActive) {
                    cont.resume(data?.toByteArraySafe()?.toImageBitmapOrNull())
                }
            }
        }
    }

    cont.invokeOnCancellation {
        manager.cancelImageRequest(requestId)
    }
}

private fun ByteArray?.toImageBitmapOrNull(): ImageBitmap? {
    if (this == null || isEmpty()) return null
    return runCatching { toImageBitmap() }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArraySafe(): ByteArray {
    val length = this.length.toInt()
    if (length <= 0) return ByteArray(0)
    val byteArray = ByteArray(length)
    val source = this.bytes ?: return ByteArray(0)
    byteArray.usePinned {
        memcpy(it.addressOf(0), source, length.convert())
    }
    return byteArray
}

@OptIn(ExperimentalForeignApi::class)
private fun isAtLeastIOS13(): Boolean {
    val version: CValue<NSOperatingSystemVersion> = NSProcessInfo.processInfo.operatingSystemVersion
    return version.useContents { majorVersion >= 13 }
}

