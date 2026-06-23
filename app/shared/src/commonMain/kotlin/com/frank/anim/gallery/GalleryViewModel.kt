package com.frank.anim.gallery

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.frank.anim.shared.BaseViewModel

/**
 * 相册ViewModel
 */
class GalleryViewModel : BaseViewModel() {

    // 注意：这些实例化应该在平台特定模块中进行
    // 这里使用延迟初始化来避免编译错误
    private val galleryProvider by lazy { PlatformGalleryProvider() }
    private val permissionManager by lazy { PlatformGalleryPermissionManager() }

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    init {
        checkPermissionStatus()
    }

    /**
     * 检查权限状态
     */
    fun checkPermissionStatus() {
        _permissionState.value = _permissionState.value.copy(
            hasPermission = permissionManager.hasGalleryPermission(),
            statusText = permissionManager.getPermissionStatusText()
        )
    }

    /**
     * 直接授予权限（用于权限说明确认后）
     */
    fun grantPermissionDirectly() {
        _permissionState.value = _permissionState.value.copy(
            hasPermission = true,
            statusText = "已获取相册权限",
            isLoading = false,
            error = null
        )

        // 权限授予后立即加载相册数据
        loadGalleryImages()
        loadGalleryAlbums()
    }

    /**
     * 请求相册权限（强制获取真实照片）
     */
    fun requestPermission() {
        viewModelScope.launch {
            _permissionState.value = _permissionState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                permissionManager.requestGalleryPermission().collect { hasPermission ->
                    _permissionState.value = _permissionState.value.copy(
                        hasPermission = hasPermission,
                        statusText = permissionManager.getPermissionStatusText(),
                        isLoading = false
                    )

                    // 如果获取了权限，立即加载真实的相册数据
                    if (hasPermission) {
                        loadGalleryImages()
                        loadGalleryAlbums()
                    } else {
                        // 如果权限被拒绝，显示明确的错误信息
                        _permissionState.value = _permissionState.value.copy(
                            error = "需要相册权限才能显示您的真实照片，请在权限对话框中选择'允许'"
                        )
                    }
                }
            } catch (e: Exception) {
                _permissionState.value = _permissionState.value.copy(
                    isLoading = false,
                    error = "权限请求失败，请重试: ${e.message}"
                )
            }
        }
    }

    /**
     * 打开应用设置
     */
    fun openAppSettings() {
        viewModelScope.launch {
            val success = permissionManager.openAppSettings()
            if (!success) {
                _permissionState.value = _permissionState.value.copy(
                    error = "无法打开应用设置"
                )
            }
        }
    }

    /**
     * 加载相册图片
     */
    fun loadGalleryImages(refresh: Boolean = false) {
        if (!_permissionState.value.hasPermission) {
            _uiState.value = _uiState.value.copy(
                error = "没有相册访问权限"
            )
            return
        }

        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    images = if (refresh) _uiState.value.images else emptyList() // 刷新时保持原数据
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val result = if (refresh) {
                    galleryProvider.refreshGallery()
                    galleryProvider.getGalleryImages()
                } else {
                    galleryProvider.getGalleryImages()
                }

                result.fold(
                    onSuccess = { images ->
                        _uiState.value = _uiState.value.copy(
                            images = images,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "加载相册失败: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载相册失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 加载相册文件夹
     */
    fun loadGalleryAlbums() {
        if (!_permissionState.value.hasPermission) {
            return
        }

        viewModelScope.launch {
            try {
                val result = galleryProvider.getGalleryAlbums()
                result.fold(
                    onSuccess = { albums ->
                        _uiState.value = _uiState.value.copy(
                            albums = albums
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "加载相册文件夹失败: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载相册文件夹失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 获取最新图片
     */
    fun loadLatestImages(limit: Int = 20) {
        if (!_permissionState.value.hasPermission) {
            _uiState.value = _uiState.value.copy(
                error = "没有相册访问权限"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result = galleryProvider.getLatestImages(limit)
                result.fold(
                    onSuccess = { images ->
                        _uiState.value = _uiState.value.copy(
                            images = images,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "加载最新图片失败: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载最新图片失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 搜索图片
     */
    fun searchImages(query: String) {
        if (!_permissionState.value.hasPermission) {
            _uiState.value = _uiState.value.copy(
                error = "没有相册访问权限"
            )
            return
        }

        if (query.isBlank()) {
            loadGalleryImages()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result = galleryProvider.searchImages(query)
                result.fold(
                    onSuccess = { images ->
                        _uiState.value = _uiState.value.copy(
                            images = images,
                            isLoading = false,
                            searchQuery = query,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "搜索图片失败: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "搜索图片失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 切换视图模式
     */
    fun toggleViewMode() {
        val currentMode = _uiState.value.viewMode
        val newMode = when (currentMode) {
            GalleryViewMode.GRID -> GalleryViewMode.LIST
            GalleryViewMode.LIST -> GalleryViewMode.GRID
        }
        _uiState.value = _uiState.value.copy(
            viewMode = newMode
        )
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        _permissionState.value = _permissionState.value.copy(error = null)
    }

    /**
     * 选择图片
     */
    fun selectImage(image: GalleryImage) {
        val selectedImages = _uiState.value.selectedImages.toMutableSet()
        if (selectedImages.contains(image)) {
            selectedImages.remove(image)
        } else {
            selectedImages.add(image)
        }
        _uiState.value = _uiState.value.copy(
            selectedImages = selectedImages
        )
    }

    /**
     * 清除选择
     */
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedImages = emptySet()
        )
    }

    /**
     * 获取平台信息
     */
    fun getPlatformInfo(): String {
        return PlatformInfoProvider().getPlatformInfo()
    }
}

/**
 * 相册UI状态
 */
data class GalleryUiState(
    val images: List<GalleryImage> = emptyList(),
    val albums: List<GalleryAlbum> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewMode: GalleryViewMode = GalleryViewMode.GRID,
    val searchQuery: String = "",
    val selectedImages: Set<GalleryImage> = emptySet(),
    val currentAlbum: GalleryAlbum? = null
)

/**
 * 权限状态
 */
data class PermissionState(
    val hasPermission: Boolean = false,
    val isLoading: Boolean = false,
    val statusText: String = "",
    val error: String? = null
)

/**
 * 相册视图模式
 */
enum class GalleryViewMode {
    GRID,   // 网格模式
    LIST    // 列表模式
}