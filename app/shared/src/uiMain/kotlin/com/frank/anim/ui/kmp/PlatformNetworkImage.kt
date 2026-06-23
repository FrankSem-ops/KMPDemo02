package com.frank.anim.ui.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier
)
