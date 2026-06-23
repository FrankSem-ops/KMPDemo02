package com.frank.anim.ui.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier
) {
    RemoteImagePlaceholder(
        text = "Web 网络图片\n$url",
        modifier = modifier
    )
}
