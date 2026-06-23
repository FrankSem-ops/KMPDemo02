@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.frank.anim.gallery

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class PlatformGalleryProvider actual constructor() {
    actual suspend fun getGalleryImages(): Result<List<GalleryImage>> = Result.success(emptyList())
    actual suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>> = Result.success(emptyList())
    actual suspend fun getImagesInAlbum(albumId: String): Result<List<GalleryImage>> = Result.success(emptyList())
    actual suspend fun getImageById(imageId: String): GalleryImage? = null
    actual suspend fun getLatestImages(limit: Int): Result<List<GalleryImage>> = Result.success(emptyList())
    actual suspend fun searchImages(query: String): Result<List<GalleryImage>> = Result.success(emptyList())
    actual suspend fun refreshGallery(): Result<Unit> = Result.success(Unit)
}

actual class PlatformGalleryPermissionManager actual constructor() {
    actual fun hasGalleryPermission(): Boolean = true
    actual fun requestGalleryPermission(): Flow<Boolean> = flowOf(true)
    actual suspend fun openAppSettings(): Boolean = false
    actual fun getPermissionStatusText(): String = "Web 端未接入浏览器图库，展示为降级模式"
}

actual class PlatformInfoProvider actual constructor() {
    actual fun getPlatformInfo(): String = "JavaScript"
}
