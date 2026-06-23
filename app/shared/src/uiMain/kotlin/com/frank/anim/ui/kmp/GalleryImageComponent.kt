package com.frank.anim.ui.kmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.frank.anim.gallery.GalleryImage

/**
 * 相册图片显示组件
 */
@Composable
fun GalleryImageComponent(
    image: GalleryImage,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: () -> Unit = {}
) {
    // 使用平台特定的图片加载组件
    PlatformImageLoader(
        imageUrl = image.uri,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)),
        contentScale = contentScale,
        onClick = onClick
    )
}

/**
 * 平台特定的图片加载器
 */
@Composable
expect fun PlatformImageLoader(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: () -> Unit = {}
)

/**
 * 图片加载状态
 */
enum class ImageLoadState {
    Loading,
    Success,
    Error
}

/**
 * 图片加载状态数据类
 */
data class ImageLoadResult(
    val state: ImageLoadState,
    val bitmap: ImageBitmap? = null,
    val error: String? = null
)