@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.frank.anim.gallery

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.frank.anim.platform.AndroidContextHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * Android平台相册提供器实现
 */
actual class PlatformGalleryProvider actual constructor() {

    private val context: Context = AndroidContextHolder.context

    /**
     * 拉取系统图库中的所有图片，以添加时间降序排列返回。
     * 外部调用前需要确保已经拥有相册读取权限。
     */
    actual suspend fun getGalleryImages(): Result<List<GalleryImage>> {
        return try {
            Log.d("GalleryProvider", "开始获取相册图片")

            // 检查权限状态
            val hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            Log.d("GalleryProvider", "权限状态: $hasReadPermission")

            if (!hasReadPermission) {
                Log.e("GalleryProvider", "没有相册读取权限")
                return Result.failure(Exception("没有相册读取权限，请授予权限后重试"))
            }

            // 用于收集查询到的图片数据，最终统一转换成KMP可用的数据模型
            val images = mutableListOf<GalleryImage>()

            // 指定需要从MediaStore读取的字段，减少不必要的数据传输
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.ORIENTATION
            )

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            Log.d("GalleryProvider", "使用URI: $uri")

            // 查询系统媒体库，按照添加时间降序排列
            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )

            Log.d("GalleryProvider", "查询结果: ${cursor?.count ?: 0} 张图片")

            cursor?.use {
                // 提前解析字段索引，避免在循环中重复查找列索引
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val widthColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val orientationColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)

                // 游标逐行读取图片信息，将原始数据转换成跨平台模型
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getLong(sizeColumn)
                    val mimeType = it.getString(mimeTypeColumn) ?: ""
                    val width = it.getInt(widthColumn)
                    val height = it.getInt(heightColumn)
                    val dateAdded = it.getLong(dateAddedColumn)
                    val dateModified = it.getLong(dateModifiedColumn)
                    val orientation = it.getInt(orientationColumn)

                    val contentUri = ContentUris.withAppendedId(uri, id)

                    Log.d("GalleryProvider", "找到图片: $name, URI: $contentUri")

                    // 将原生字段组装为跨平台依赖的数据模型
                    images.add(
                        GalleryImage(
                            id = id.toString(),
                            name = name,
                            uri = contentUri.toString(),
                            path = contentUri.path ?: "",
                            size = size,
                            mimeType = mimeType,
                            width = width,
                            height = height,
                            dateAdded = dateAdded * 1000,
                            dateModified = dateModified * 1000,
                            orientation = orientation,
                            isVideo = mimeType.startsWith("video/")
                        )
                    )
                }
            } ?: run {
                Log.e("GalleryProvider", "查询返回null cursor")
            }

            Log.d("GalleryProvider", "成功获取 ${images.size} 张图片")
            Result.success(images)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 根据 `MediaStore` 的 bucket 概念，统计每个相册文件夹包含的图片数量。
     * 当前实现仅提供基本信息（未设置封面）。
     */
    actual suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>> = try {
        // 用于收集相册文件夹（按bucket分类）
        val albums = mutableListOf<GalleryAlbum>()

        // 相册（bucket）查询只需要ID和显示名称
        val bucketProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        // 查询每个bucket的信息，并统计数量
        context.contentResolver.query(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            },
            bucketProjection,
            null,
            null,
            null
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            // 将同一个bucket的图片数量累加
            val bucketMap: MutableMap<String, Int> = mutableMapOf()

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn) ?: "Unknown"
                bucketMap[bucketId] = bucketMap.getOrDefault(bucketId, 0) + 1
            }

            // 将统计结果转换为跨平台的 `GalleryAlbum` 模型
            bucketMap.forEach { (bucketId: String, count: Int) ->
                val bucketName = if (bucketId == "-1") "所有图片" else "相册 $bucketId"
                albums.add(
                    GalleryAlbum(
                        id = bucketId,
                        name = bucketName,
                        coverImage = null,
                        imageCount = count,
                        dateCreated = System.currentTimeMillis()
                    )
                )
            }
        }

        Result.success(albums)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    /**
     * 获取指定相册内的图片。目前尚未实现，返回空列表。
     * 可以在后续版本中使用 `bucket_id` 作为查询条件进一步完善。
     */
    actual suspend fun getImagesInAlbum(albumId: String): Result<List<GalleryImage>> {
        return Result.success(emptyList())
    }

    /**
     * 通过 `imageId` 在已有的缓存结果中检索图片。
     * 如果 `getGalleryImages` 失败或数据不存在，返回 `null`。
     */
    actual suspend fun getImageById(imageId: String): GalleryImage? {
        return try {
            getGalleryImages().getOrNull()?.find { it.id == imageId }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取最近导入的若干张图片（调用 `getGalleryImages` 后取前 `limit` 张）。
     */
    actual suspend fun getLatestImages(limit: Int): Result<List<GalleryImage>> {
        return getGalleryImages().map { images ->
            images.take(limit)
        }
    }

    /**
     * 根据图片名称进行简单的本地模糊匹配。
     */
    actual suspend fun searchImages(query: String): Result<List<GalleryImage>> {
        return getGalleryImages().map { images ->
            images.filter { image ->
                image.name.contains(query, ignoreCase = true)
            }
        }
    }

    actual suspend fun refreshGallery(): Result<Unit> {
        return Result.success(Unit)
    }
}

/**
 * Android权限管理器实现
 */
actual class PlatformGalleryPermissionManager actual constructor() {

    private val context: Context = AndroidContextHolder.context
    private val permissionManager = PermissionRequestManager.getInstance()

    /**
     * 同步判断当前应用是否已经具备访问相册的权限。
     */
    actual fun hasGalleryPermission(): Boolean {
        return permissionManager.hasPermissions(context)
    }

    actual fun requestGalleryPermission(): Flow<Boolean> = callbackFlow {
        if (permissionManager.hasPermissions(context)) {
            trySend(true)
            close()
            return@callbackFlow
        }

        val activity = AndroidContextHolder.getActivity()
        if (activity == null) {
            Log.w("GalleryPermission", "当前没有可用的Activity，无法请求权限")
            trySend(false)
            close()
            return@callbackFlow
        }

        val collectorScope = CoroutineScope(Dispatchers.Main.immediate)
        val job = permissionManager.permissionResult
            .onEach { granted ->
                if (granted != null) {
                    trySend(granted)
                    permissionManager.reset()
                    close()
                }
            }
            .launchIn(collectorScope)

        permissionManager.requestPermissionsFromActivity(activity)

        awaitClose {
            job.cancel()
        }
    }.flowOn(Dispatchers.Main)

    /**
     * 引导用户跳转到系统设置页手动开启权限。
     */
    actual suspend fun openAppSettings(): Boolean {
        return try {
            val intent = android.content.Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                android.net.Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    actual fun getPermissionStatusText(): String {
        return permissionManager.getPermissionStatusText()
    }
}

/**
 * Android平台信息提供器实现
 */
actual class PlatformInfoProvider actual constructor() {

    private val context: Context = AndroidContextHolder.context

    /**
     * 拼接当前系统、设备和应用版本信息。
     */
    @RequiresApi(Build.VERSION_CODES.P)
    actual fun getPlatformInfo(): String {
        return StringBuilder()
            .append("Android 平台信息\n")
            .append("系统版本: ").append(getOSVersion()).append('\n')
            .append("设备型号: ").append(getDeviceModel()).append('\n')
            .append("应用版本: ").append(getAppVersion()).append('\n')
            .append("调试模式: ").append(if (isDebugMode()) "是" else "否").append('\n')
            .append("时区: ").append(getTimeZone())
            .toString()
    }

    private fun getOSVersion(): String {
        return "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    private fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getAppVersion(): String {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: Exception) {
            "未知版本"
        }
    }

    private fun isDebugMode(): Boolean {
        return 0 != (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE)
    }

    private fun getTimeZone(): String {
        return java.util.TimeZone.getDefault().id
    }

}
