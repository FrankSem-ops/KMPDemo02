package com.frank.anim.ui.kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun PlatformImageLoader(
    imageUrl: String,
    modifier: Modifier,
    contentScale: ContentScale,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        RemoteImagePlaceholder(
            text = "Web 相册预览\n$imageUrl",
            modifier = Modifier.matchParentSize()
        )
    }
}
