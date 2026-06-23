package com.frank.anim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.frank.anim.gallery.GalleryImage
import com.frank.anim.gallery.GalleryViewMode
import com.frank.anim.gallery.GalleryViewModel
import com.frank.anim.gallery.GalleryUiState
import com.frank.anim.gallery.PermissionState
import com.frank.anim.ui.components.AppSymbolIcon
import com.frank.anim.ui.components.AppSymbols
import com.frank.anim.ui.kmp.GalleryImageComponent
/**
 * 相册管理页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryPage(
    onBack: () -> Unit
) {
    val viewModel = remember { GalleryViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val permissionState by viewModel.permissionState.collectAsState()

    // 权限说明弹窗状态
    var showPermissionRationale by remember { mutableStateOf(false) }

    // 启动时检查权限
    LaunchedEffect(Unit) {
        if (permissionState.hasPermission) {
            viewModel.loadGalleryImages()
            viewModel.loadGalleryAlbums()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!permissionState.hasPermission) {
            PermissionRequestPage(
                permissionState = permissionState,
                onRequestPermission = { showPermissionRationale = true },
                onOpenSettings = { viewModel.openAppSettings() },
                onRetryPermission = { viewModel.checkPermissionStatus() }
            )
        } else {
            GalleryContent(
                uiState = uiState,
                viewModel = viewModel,
                onBack = onBack
            )
        }

        // 错误提示
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                viewModel.clearError()
            }
        }

        permissionState.error?.let { error ->
            LaunchedEffect(error) {
                viewModel.clearError()
            }
        }

        // 权限说明弹窗
        if (showPermissionRationale) {
            PermissionRationaleDialog(
                onConfirm = {
                },
                onDismiss = {
                    showPermissionRationale = false
                },
                onRequestRealPermission = {
                    showPermissionRationale = false
                    viewModel.requestPermission()
                }
            )
        }
    }
}

/**
 * 权限请求页面
 */
@Composable
fun PermissionRequestPage(
    permissionState: PermissionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetryPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 权限图标
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Color(0xFFE3F2FD),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AppSymbolIcon(
                symbol = AppSymbols.Info,
                contentDescription = "相册权限",
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 标题
        Text(
            text = "需要相册访问权限",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 说明文字
        Text(
            text = "为了访问和展示您的照片，此应用需要相册访问权限。\n\n${permissionState.statusText}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 权限请求按钮
        if (permissionState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Column {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("授予权限")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("打开设置")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onRetryPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("重新检查权限")
                }
            }
        }
    }
}

/**
 * 相册内容页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryContent(
    uiState: GalleryUiState,
    viewModel: GalleryViewModel,
    onBack: () -> Unit
) {
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部工具栏
        TopAppBar(
            title = { Text("相册管理") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Back,
                        contentDescription = "返回"
                    )
                }
            },
            actions = {
                // 搜索按钮
                IconButton(onClick = { showSearch = !showSearch }) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Search,
                        contentDescription = "搜索"
                    )
                }

                // 视图模式切换
                IconButton(onClick = { viewModel.toggleViewMode() }) {
                    AppSymbolIcon(
                        symbol = when (uiState.viewMode) {
                            GalleryViewMode.GRID -> AppSymbols.List
                            GalleryViewMode.LIST -> AppSymbols.Settings
                        },
                        contentDescription = "切换视图"
                    )
                }

                // 刷新按钮
                IconButton(onClick = { viewModel.loadGalleryImages(refresh = true) }) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        AppSymbolIcon(
                            symbol = AppSymbols.Refresh,
                            contentDescription = "刷新"
                        )
                    }
                }
            }
        )

        // 搜索栏
        if (showSearch) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    viewModel.searchImages(query)
                },
                onActiveChange = { active ->
                    if (!active) {
                        searchQuery = ""
                        viewModel.loadGalleryImages()
                    }
                },
                active = false,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索照片...") },
                leadingIcon = {
                    AppSymbolIcon(
                        symbol = AppSymbols.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.loadGalleryImages()
                        }) {
                            AppSymbolIcon(
                                symbol = AppSymbols.Clear,
                                contentDescription = "清除"
                            )
                        }
                    }
                }
            ) {}
        }

        // 相册统计信息
        GalleryStats(
            images = uiState.images,
            selectedCount = uiState.selectedImages.size,
            searchQuery = uiState.searchQuery,
            onClearSelection = { viewModel.clearSelection() },
            onLoadLatest = { viewModel.loadLatestImages() }
        )

        // 相册内容
        when {
            uiState.isLoading && uiState.images.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "加载相册中...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            uiState.images.isEmpty() -> {
                EmptyGalleryState(
                    searchQuery = uiState.searchQuery,
                    onRetry = { viewModel.loadGalleryImages() }
                )
            }

            else -> {
                when (uiState.viewMode) {
                    GalleryViewMode.GRID -> GridView(
                        images = uiState.images,
                        selectedImages = uiState.selectedImages,
                        onImageClick = { image -> viewModel.selectImage(image) }
                    )
                    GalleryViewMode.LIST -> ListView(
                        images = uiState.images,
                        selectedImages = uiState.selectedImages,
                        onImageClick = { image -> viewModel.selectImage(image) }
                    )
                }
            }
        }
    }
}

/**
 * 相册统计信息
 */
@Composable
fun GalleryStats(
    images: List<GalleryImage>,
    selectedCount: Int,
    searchQuery: String,
    onClearSelection: () -> Unit,
    onLoadLatest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (searchQuery.isNotEmpty()) {
                        "搜索结果: ${images.size} 张"
                    } else {
                        "相册照片: ${images.size} 张"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (selectedCount > 0) {
                    Text(
                        text = "已选择 $selectedCount 张",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                if (selectedCount > 0) {
                    TextButton(onClick = onClearSelection) {
                        Text("清除选择")
                    }
                }

                TextButton(onClick = onLoadLatest) {
                    Text("最新照片")
                }
            }
        }
    }
}

/**
 * 空相册状态
 */
@Composable
fun EmptyGalleryState(
    searchQuery: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSymbolIcon(
                symbol = if (searchQuery.isNotEmpty()) AppSymbols.Search else AppSymbols.Info,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )

            Text(
                text = if (searchQuery.isNotEmpty()) "没有找到匹配的照片" else "相册为空",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Text(
                text = if (searchQuery.isNotEmpty()) {
                    "尝试使用不同的关键词搜索"
                } else {
                    "您的相册中还没有照片"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetry) {
                AppSymbolIcon(
                    symbol = AppSymbols.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("重新加载")
            }
        }
    }
}

/**
 * 网格视图
 */
@Composable
fun GridView(
    images: List<GalleryImage>,
    selectedImages: Set<GalleryImage>,
    onImageClick: (GalleryImage) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(images, key = { it.id }) { image ->
            GalleryImageItem(
                image = image,
                isSelected = selectedImages.contains(image),
                onClick = { onImageClick(image) },
                gridMode = true
            )
        }
    }
}

/**
 * 列表视图
 */
@Composable
fun ListView(
    images: List<GalleryImage>,
    selectedImages: Set<GalleryImage>,
    onImageClick: (GalleryImage) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images, key = { it.id }) { image ->
            GalleryImageItem(
                image = image,
                isSelected = selectedImages.contains(image),
                onClick = { onImageClick(image) },
                gridMode = false
            )
        }
    }
}

/**
 * 相册图片项
 */
@Composable
fun GalleryImageItem(
    image: GalleryImage,
    isSelected: Boolean,
    onClick: () -> Unit,
    gridMode: Boolean
) {
    Card(
        modifier = Modifier
            .then(if (gridMode) {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            })
            .clickable { onClick() },
        shape = if (gridMode) RoundedCornerShape(8.dp) else RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box {
            // 使用实际的图片加载组件
            GalleryImageComponent(
                image = image,
                modifier = Modifier.fillMaxSize(),
                onClick = onClick
            )

            // 视频标记
            if (image.isVideo) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Play,
                        contentDescription = "视频",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }

            // 选择标记
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppSymbolIcon(
                        symbol = AppSymbols.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }

            // 列表模式显示文件名
            if (!gridMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = image.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
