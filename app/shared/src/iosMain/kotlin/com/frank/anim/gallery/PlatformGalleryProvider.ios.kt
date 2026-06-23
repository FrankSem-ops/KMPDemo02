package com.frank.anim.gallery

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import platform.Foundation.NSDate
import platform.Foundation.NSNumber
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo
import kotlinx.cinterop.CValue
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.Foundation.NSURL
import platform.Foundation.NSSortDescriptor
import platform.Foundation.NSTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.Photos.PHAccessLevelReadWrite
import platform.Photos.PHAsset
import platform.Photos.PHAssetCollection
import platform.Photos.PHAssetCollectionSubtypeAny
import platform.Photos.PHAssetCollectionTypeSmartAlbum
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHAssetMediaTypeVideo
import platform.Photos.PHAssetResource
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHFetchOptions
import platform.Photos.PHFetchResult
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS平台下的相册数据读取实现：
 * - 使用 Apple Photos 框架 (`PHAsset`/`PHPhotoLibrary`) 访问本地媒体
 */
actual class PlatformGalleryProvider actual constructor() {

    /**
     * 拉取所有图片/视频资源，按创建时间降序排列返回。
     */
    actual suspend fun getGalleryImages(): Result<List<GalleryImage>> = withContext(Dispatchers.Default) {
        try {
            if (!isAuthorized(currentAuthorizationStatus())) {
                return@withContext Result.failure(Exception("没有相册访问权限"))
            }

            // 设置查询选项，按 creationDate 降序排列，与 Android MediaStore 行为对齐
            val fetchOptions = PHFetchOptions().apply {
                sortDescriptors = listOf(NSSortDescriptor(key = "creationDate", ascending = false))
            }

            val fetchResult = PHAsset.fetchAssetsWithOptions(fetchOptions)
            val images = mutableListOf<GalleryImage>()
            val count = fetchResult.count.toInt()

            // 遍历查询结果，将 `PHAsset` 转换为跨平台模型
            for (index in 0 until count) {
                val asset = fetchResult.objectAtIndex(index.toULong()) as? PHAsset ?: continue
                if (asset.mediaType != PHAssetMediaTypeImage && asset.mediaType != PHAssetMediaTypeVideo) continue
                images.add(asset.toGalleryImage())
            }

            Result.success(images)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 汇总 iOS 端常用相册集合（系统智能相册 + 所有照片），保持与 Android `bucket` 概念类似。
     */
    actual suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>> = withContext(Dispatchers.Default) {
        try {
            if (!isAuthorized(currentAuthorizationStatus())) {
                return@withContext Result.failure(Exception("没有相册访问权限"))
            }

            val albums = mutableListOf<GalleryAlbum>()
            val now = (NSDate().timeIntervalSince1970 * 1000).toLong()

            val allAssets = PHAsset.fetchAssetsWithMediaType(PHAssetMediaTypeImage, options = null)
            val coverAsset = allAssets.firstAsset()
            albums.add(
                GalleryAlbum(
                    id = "all_photos",
                    name = "所有照片",
                    coverImage = coverAsset?.toGalleryImage(),
                    imageCount = allAssets.count.toInt(),
                    dateCreated = now
                )
            )

            // 读取系统智能相册，填充更多相册项
            val collections = PHAssetCollection.fetchAssetCollectionsWithType(
                type = PHAssetCollectionTypeSmartAlbum,
                subtype = PHAssetCollectionSubtypeAny,
                options = null
            )

            val collectionCount = collections.count.toInt()
            for (index in 0 until collectionCount) {
                val collection = collections.objectAtIndex(index.toULong()) as? PHAssetCollection ?: continue
                val assets = PHAsset.fetchAssetsInAssetCollection(collection, options = null)
                if (assets.count == 0uL) continue

                val firstAsset = assets.firstAsset()
                albums.add(
                    GalleryAlbum(
                        id = collection.localIdentifier,
                        name = collection.localizedTitle ?: "相册",
                        coverImage = firstAsset?.toGalleryImage(),
                        imageCount = assets.count.toInt(),
                        dateCreated = now
                    )
                )
            }

            Result.success(albums)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * 根据 `albumId`（即 Photos localIdentifier）拉取指定相册内容。
     * 与 Android 实现互补，确保共享接口能在双端同时使用。
     */
    actual suspend fun getImagesInAlbum(albumId: String): Result<List<GalleryImage>> = withContext(Dispatchers.Default) {
        try {
            if (!isAuthorized(currentAuthorizationStatus())) {
                return@withContext Result.failure(Exception("没有相册访问权限"))
            }

            val collections = PHAssetCollection.fetchAssetCollectionsWithLocalIdentifiers(listOf(albumId), null)
            val collection = collections.firstCollection() ?: return@withContext Result.success(emptyList())

            val assets = PHAsset.fetchAssetsInAssetCollection(collection, options = null)
            val images = mutableListOf<GalleryImage>()
            val count = assets.count.toInt()
            for (index in 0 until count) {
                val asset = assets.objectAtIndex(index.toULong()) as? PHAsset ?: continue
                if (asset.mediaType != PHAssetMediaTypeImage && asset.mediaType != PHAssetMediaTypeVideo) continue
                images.add(asset.toGalleryImage())
            }

            Result.success(images)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据 Photos 的 `localIdentifier` 查询单张媒体资源。
     */
    actual suspend fun getImageById(imageId: String): GalleryImage? = withContext(Dispatchers.Default) {
        val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(listOf(imageId), null)
        fetchResult.firstAsset()?.toGalleryImage()
    }

    /**
     * 取最新的若干张图片。直接复用 `getGalleryImages` 的结果，逻辑与 Android 对齐。
     */
    actual suspend fun getLatestImages(limit: Int): Result<List<GalleryImage>> {
        return getGalleryImages().map { images -> images.take(limit) }
    }

    /**
     * 按名称做简单模糊搜索，与 Android 端保持一致。
     */
    actual suspend fun searchImages(query: String): Result<List<GalleryImage>> {
        return getGalleryImages().map { images ->
            images.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    actual suspend fun refreshGallery(): Result<Unit> = Result.success(Unit)
}

/**
 * iOS端的相册权限管理实现：
 * - Photos 框架需要在主线程请求权限
 */
actual class PlatformGalleryPermissionManager actual constructor() {

    /**
     * 同步判断当前授权状态是否允许访问相册。
     */
    actual fun hasGalleryPermission(): Boolean = isAuthorized(currentAuthorizationStatus())

    /**
     * 发起权限请求，并通过 `Flow<Boolean>` 将结果传递给上层。
     * iOS 14 及以上使用新的 access level API，低版本自动降级。
     */
    actual fun requestGalleryPermission(): Flow<Boolean> = callbackFlow {
        val currentStatus = currentAuthorizationStatus()
        if (isAuthorized(currentStatus)) {
            trySend(true)
            close()
            return@callbackFlow
        }

        val handler: (PHAuthorizationStatus) -> Unit = { status ->
            trySend(isAuthorized(status))
            close()
        }

        dispatch_async(dispatch_get_main_queue()) {
            if (isAtLeastIOS14()) {
                PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelReadWrite, handler = handler)
            } else {
                PHPhotoLibrary.requestAuthorization(handler = handler)
            }
        }

        awaitClose { }
    }.flowOn(Dispatchers.Main)

    /**
     * 打开系统设置页，引导用户手动开启权限。
     */
    actual suspend fun openAppSettings(): Boolean {
        return try {
            val urlStr = NSURL.URLWithString("app-settings:")
            if (urlStr != null && platform.UIKit.UIApplication.sharedApplication.canOpenURL(urlStr)) {
                platform.UIKit.UIApplication.sharedApplication.openURL(urlStr)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 返回一段权限状态描述，方便 UI 显示或日志记录。
     */
    actual fun getPermissionStatusText(): String {
        val status = currentAuthorizationStatus()
        return when {
            isAuthorized(status) -> "已获取相册权限"
            status == PHAuthorizationStatusDenied -> "相册权限被拒绝，请在设置中开启"
            status == PHAuthorizationStatusRestricted -> "相册权限受限，请检查家长控制设置"
            status == PHAuthorizationStatusNotDetermined -> "相册权限未确定，需要请求权限"
            else -> "未知权限状态"
        }
    }
}

/**
 * iOS 平台信息提供器，与 Android 端对应，为界面提供环境信息。
 */
actual class PlatformInfoProvider actual constructor() {

    /**
     * 汇总系统、设备、App版本等信息。
     */
    actual fun getPlatformInfo(): String = buildString {
        appendLine("iOS 平台信息")
        appendLine("系统版本: ${getOSVersion()}")
        appendLine("设备型号: ${getDeviceModel()}")
        appendLine("应用版本: ${getAppVersion()}")
        appendLine("调试模式: ${if (isDebugMode()) "是" else "否"}")
        appendLine("时区: ${getTimeZone()}")
    }

    private fun getOSVersion(): String {
        val device = platform.UIKit.UIDevice.currentDevice
        return "${device.systemName} ${device.systemVersion}"
    }

    private fun getDeviceModel(): String {
        val device = platform.UIKit.UIDevice.currentDevice
        return "${device.model} (${device.systemVersion})"
    }

    private fun getAppVersion(): String {
        return try {
            val bundle = platform.Foundation.NSBundle.mainBundle
            val version = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "未知"
            val build = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "0"
            "$version ($build)"
        } catch (e: Exception) {
            "未知版本"
        }
    }

    private fun isDebugMode(): Boolean = true

    private fun getTimeZone(): String = "UTC"
}

private fun currentAuthorizationStatus(): PHAuthorizationStatus {
    return if (isAtLeastIOS14()) {
        PHPhotoLibrary.authorizationStatusForAccessLevel(PHAccessLevelReadWrite)
    } else {
        PHPhotoLibrary.authorizationStatus()
    }
}

/**
 * 针对 iOS 14+ 存在“限制”权限（Limited），也当作已授权处理。
 */
private fun isAuthorized(status: PHAuthorizationStatus): Boolean {
    return if (isAtLeastIOS14()) {
        status == PHAuthorizationStatusAuthorized || status == PHAuthorizationStatusLimited
    } else {
        status == PHAuthorizationStatusAuthorized
    }
}

private fun PHAsset.toGalleryImage(): GalleryImage {
    // 通过 PHAssetResource 读取原始文件信息，填充跨平台结构体
    val resources = PHAssetResource.assetResourcesForAsset(this)
    val resource = resources.firstOrNull() as? PHAssetResource
    val fileName = resource?.originalFilename ?: "IMG_${localIdentifier.take(12)}"
    val fileSize = resource?.estimatedFileSize() ?: 0L
    val mimeType = resource?.uniformTypeIdentifier ?: if (mediaType == PHAssetMediaTypeVideo) "video/*" else "image/*"

    val created = creationDate?.timeIntervalSince1970?.times(1000)?.toLong() ?: 0L
    val modified = modificationDate?.timeIntervalSince1970?.times(1000)?.toLong() ?: created

    return GalleryImage(
        id = localIdentifier,
        name = fileName,
        uri = localIdentifier,
        path = localIdentifier,
        size = fileSize,
        mimeType = mimeType,
        width = pixelWidth.toInt(),
        height = pixelHeight.toInt(),
        dateAdded = created,
        dateModified = modified,
        orientation = 0,
        isVideo = mediaType == PHAssetMediaTypeVideo,
        duration = if (mediaType == PHAssetMediaTypeVideo) (duration * 1000).toLong() else null
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun isAtLeastIOS14(): Boolean {
    val processInfo = NSProcessInfo.processInfo
    val version: CValue<NSOperatingSystemVersion> = processInfo.operatingSystemVersion
    return version.useContents { majorVersion >= 14 }
}

private fun PHFetchResult.firstAsset(): PHAsset? {
    return if (count.toInt() > 0) objectAtIndex(0uL) as? PHAsset else null
}

private fun PHFetchResult.firstCollection(): PHAssetCollection? {
    return if (count.toInt() > 0) objectAtIndex(0uL) as? PHAssetCollection else null
}

@OptIn(ExperimentalForeignApi::class)
private fun PHAssetResource.estimatedFileSize(): Long {
    return 0L // 简化实现，暂时返回0
}

