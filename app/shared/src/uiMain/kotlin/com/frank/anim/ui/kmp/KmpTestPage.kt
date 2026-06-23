package com.frank.anim.ui.kmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frank.anim.network.createDemoApiClient
import com.frank.anim.network.model.Photo
import com.frank.anim.ui.components.AppSymbolIcon
import com.frank.anim.ui.components.AppSymbols
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun KmpFeatureMenuPage(
    onBack: () -> Unit,
    onNavigateToCommonLibs: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KMP 双端适配示例",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppSymbolIcon(
                            symbol = AppSymbols.Back,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "选择要体验的跨平台功能",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNavigateToCommonLibs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "常用三方库适配示例",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onNavigateToGallery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "双端相册访问示例",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "使用说明",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "• 常用三方库适配示例：展示 KMP 共享网络请求、跨平台图片解码、平台文件保存等能力\n" +
                            "• 双端相册访问示例：演示 KMP 在 Android/iOS 下统一权限与相册访问流程，Desktop/Web 下提供降级体验",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun rememberKmpTestViewModel(): KmpTestViewModel {
    val viewModel = remember { KmpTestViewModel(createDemoApiClient()) }
    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }
    return viewModel
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun KmpTestPage(onBack: () -> Unit) {
    val viewModel = rememberKmpTestViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "KMP 网络能力演示") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppSymbolIcon(
                            symbol = AppSymbols.Back,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.errorMessage != null -> {
                    ErrorView(
                        message = state.errorMessage ?: "Unknown error",
                        onRetry = { viewModel.refresh() }
                    )
                }

                state.photos.isEmpty() -> {
                    EmptyView(onRefresh = { viewModel.refresh() })
                }

                else -> {
                    PhotoList(
                        photos = state.photos,
                        token = state.token,
                        fromCache = state.isUsingCache,
                        onRefresh = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoList(
    photos: List<Photo>,
    token: String?,
    fromCache: Boolean,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        val hideExplanation by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 80
            }
        }

        AnimatedVisibility(
            visible = !hideExplanation,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "演示说明",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "此页面展示 Kotlin Multiplatform (KMP) 在多个端之间共享网络请求、状态管理和图片处理逻辑。\n" +
                            "为了适配当前新壳项目，这里保留了旧 Demo 的交互结构，但将底层实现收敛为轻量级跨平台版本，更适合 Android、iOS、Desktop、Web 一起接入。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (fromCache) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "当前显示的是本轮会话内缓存数据",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        token?.let { tokenValue ->
            AnimatedVisibility(
                visible = !hideExplanation,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Text(
                    text = "Session Token: $tokenValue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        Button(
            onClick = onRefresh,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "刷新图片列表")
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ImageDownloadTestCard()
            }

            items(photos, key = { it.id }) { photo ->
                PhotoCard(photo)
            }
        }
    }
}

@Composable
private fun PhotoCard(photo: Photo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (photo.imageData != null || !photo.imageUrl.isNullOrEmpty()) {
                Text(
                    text = "原图",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RemoteImage(
                    url = photo.imageUrl,
                    cachedData = photo.imageData,
                    contentDescription = photo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
            if (photo.thumbnailData != null || !photo.thumbnailUrl.isNullOrEmpty()) {
                Text(
                    text = "缩略图",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RemoteImage(
                    url = photo.thumbnailUrl,
                    cachedData = photo.thumbnailData,
                    contentDescription = "缩略图",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            }
            if (photo.imageData != null) {
                Text(
                    text = "已缓存至本轮会话内存，可重复查看",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = photo.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            photo.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                Text(
                    text = "原图地址: $url",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text(text = "重试")
        }
    }
}

@Composable
private fun EmptyView(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "暂无数据，点击刷新获取最新图片")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text(text = "刷新")
        }
    }
}

@Composable
private fun ImageDownloadTestCard() {
    val testImageUrl = "https://picsum.photos/800/480"
    val scope = rememberCoroutineScope()
    val apiClient = remember { createDemoApiClient() }

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var imageInfo by remember { mutableStateOf<String?>(null) }
    var saveResult by remember { mutableStateOf<String?>(null) }

    val imageSaver = rememberImageSaver()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "图片下载测试",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "测试URL: $testImageUrl",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            RemoteImage(
                url = if (imageBytes == null) testImageUrl else null,
                cachedData = imageBytes,
                contentDescription = "测试图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            imageInfo?.let { info ->
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        saveResult = null
                        val bytes = apiClient.downloadBytes(testImageUrl)
                        if (bytes != null) {
                            imageBytes = bytes
                            val imageSize = getImageSize(bytes)
                            val sizeKB = bytes.size / 1024.0
                            val sizeMB = sizeKB / 1024.0
                            imageInfo = buildString {
                                append("图片大小: ${formatDecimal(sizeKB)} KB (${formatDecimal(sizeMB)} MB)\n")
                                append("字节数: ${bytes.size} bytes\n")
                                if (imageSize != null) {
                                    append("图片尺寸: ${imageSize.first} x ${imageSize.second} 像素")
                                }
                            }
                        } else {
                            imageInfo = "下载失败，请检查网络或当前平台能力"
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = if (isLoading) "下载中..." else "下载图片")
            }

            if (imageBytes != null) {
                Button(
                    onClick = {
                        scope.launch {
                            val fileName = "test_image_demo"
                            val result = imageSaver.saveImage(imageBytes!!, fileName)
                            saveResult = if (result != null) {
                                "保存成功: $result"
                            } else {
                                "当前平台暂不支持保存或保存失败"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "保存到本地")
                }

                saveResult?.let { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (result.contains("成功")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

private fun formatDecimal(value: Double): String {
    val scaled = (value * 100).roundToInt() / 100.0
    return scaled.toString()
}
