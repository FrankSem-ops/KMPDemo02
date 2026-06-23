# KMP 双端相册适配实现方案

## 📋 核心架构

### 1. **统一接口层（commonMain）**
```kotlin
// 定义平台特定接口
expect class PlatformGalleryProvider() {
    suspend fun getGalleryImages(): Result<List<GalleryImage>>
    suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>>
    // ... 其他方法
}

expect class PlatformGalleryPermissionManager() {
    fun hasGalleryPermission(): Boolean
    fun requestGalleryPermission(): Flow<Boolean>
    // ... 其他方法
}
```

### 2. **平台实现层**

#### Android 端（androidMain）
- **数据源**：`MediaStore.Images.Media`
- **权限**：`READ_MEDIA_IMAGES` (Android 13+) / `READ_EXTERNAL_STORAGE` (旧版本)
- **查询方式**：`ContentResolver.query()` + `Cursor` 遍历
- **关键步骤**：
  1. 检查权限状态
  2. 构建 `MediaStore` URI
  3. 查询图片元数据（ID、名称、大小、尺寸、时间等）
  4. 转换为统一的 `GalleryImage` 模型

#### iOS 端（iosMain）
- **数据源**：`PHPhotoLibrary` + `PHAsset`
- **权限**：`PHAuthorizationStatus` (iOS 14+ 支持 Limited 权限)
- **查询方式**：`PHAsset.fetchAssetsWithOptions()` + `PHFetchResult` 遍历
- **关键步骤**：
  1. 检查授权状态（支持 Limited 权限）
  2. 创建 `PHFetchOptions` 并设置排序
  3. 遍历 `PHAsset` 获取资源信息
  4. 转换为统一的 `GalleryImage` 模型

### 3. **业务逻辑层（commonMain）**
```kotlin
class GalleryViewModel : BaseViewModel() {
    private val galleryProvider by lazy { PlatformGalleryProvider() }
    private val permissionManager by lazy { PlatformGalleryPermissionManager() }
    
    // 统一的业务逻辑，无需关心平台差异
    fun loadGalleryImages() { ... }
    fun requestPermission() { ... }
}
```

## 🔑 关键技术点

### ✅ Expect/Actual 机制
- **expect**：在 `commonMain` 声明接口，定义“期望”的平台能力
- **actual**：在 `androidMain`/`iosMain` 提供具体实现
- **优势**：编译期检查，确保双端都实现所有接口

### ✅ 统一数据模型
```kotlin
data class GalleryImage(
    val id: String,
    val name: String,
    val uri: String,
    val size: Long,
    val width: Int,
    val height: Int,
    // ... 跨平台通用字段
)
```

### ✅ 权限管理统一化
- Android：`PermissionRequestManager` 封装系统权限请求
- iOS：`PHPhotoLibrary.requestAuthorization()` 回调转 Flow
- 业务层统一使用 `Flow<Boolean>` 监听权限结果

### ✅ 错误处理
- 使用 `Result<T>` 包装返回值
- 统一错误信息格式
- 权限拒绝时提供明确的用户引导

## 📊 实现对比

| 功能 | Android | iOS |
|------|---------|-----|
| **数据源** | MediaStore | PHPhotoLibrary |
| **权限检查** | `checkSelfPermission()` | `authorizationStatus()` |
| **权限请求** | `Activity.requestPermissions()` | `requestAuthorization()` |
| **查询方式** | ContentResolver + Cursor | PHFetchResult + PHAsset |
| **排序** | SQL ORDER BY | NSSortDescriptor |
| **异步处理** | Coroutines | Coroutines + withContext |

## 🎯 使用示例

```kotlin
// 在 Compose/SwiftUI 中统一调用
val viewModel = GalleryViewModel()

// 请求权限
viewModel.requestPermission()

// 加载图片
viewModel.loadGalleryImages()

// 监听状态
viewModel.uiState.collect { state ->
    // 显示图片列表
}
```

## 💡 最佳实践

1. **接口设计**：保持 expect 接口简洁，复杂逻辑放在 commonMain
2. **数据转换**：平台层只负责数据获取和转换，不包含业务逻辑
3. **权限处理**：统一使用 Flow 异步处理，避免阻塞 UI
4. **错误处理**：使用 Result 类型，明确区分成功/失败场景
5. **性能优化**：Android 使用分页查询，iOS 使用 PHFetchOptions 限制数量

## 🚀 扩展能力

- ✅ 支持相册文件夹（Albums）
- ✅ 支持图片搜索
- ✅ 支持最新图片筛选
- ✅ 支持视频资源
- ✅ 支持权限状态实时监听
