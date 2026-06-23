package com.frank.anim.gallery

import kotlinx.coroutines.flow.Flow

/**
 * 平台特定的相册提供器接口。
 */
expect class PlatformGalleryProvider() {
    suspend fun getGalleryImages(): Result<List<GalleryImage>>
    suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>>
    suspend fun getImagesInAlbum(albumId: String): Result<List<GalleryImage>>
    suspend fun getImageById(imageId: String): GalleryImage?
    suspend fun getLatestImages(limit: Int = 20): Result<List<GalleryImage>>
    suspend fun searchImages(query: String): Result<List<GalleryImage>>
    suspend fun refreshGallery(): Result<Unit>
}

/**
 * 相册权限管理器接口。
 */
expect class PlatformGalleryPermissionManager() {
    fun hasGalleryPermission(): Boolean
    fun requestGalleryPermission(): Flow<Boolean>
    suspend fun openAppSettings(): Boolean
    fun getPermissionStatusText(): String
}

/**
 * 平台信息提供器接口。
 *
 * 用于收集调试信息或展示当前运行环境
 */
expect class PlatformInfoProvider() {
    fun getPlatformInfo(): String
}

/**
 * 相册提供器实现接口
 */
interface GalleryProvider {
    /**
     * 获取相册中的所有图片
     */
    suspend fun getGalleryImages(): Result<List<GalleryImage>>

    /**
     * 获取相册文件夹列表
     */
    suspend fun getGalleryAlbums(): Result<List<GalleryAlbum>>

    /**
     * 获取指定相册的图片
     */
    suspend fun getImagesInAlbum(albumId: String): Result<List<GalleryImage>>

    /**
     * 根据ID获取图片
     */
    suspend fun getImageById(imageId: String): GalleryImage?

    /**
     * 获取最新图片
     */
    suspend fun getLatestImages(limit: Int = 20): Result<List<GalleryImage>>

    /**
     * 搜索图片
     */
    suspend fun searchImages(query: String): Result<List<GalleryImage>>

    /**
     * 刷新相册数据
     */
    suspend fun refreshGallery(): Result<Unit>
}

/**
 * 权限管理器接口
 */
interface PermissionManager {
    /**
     * 检查是否有相册权限
     */
    fun hasGalleryPermission(): Boolean

    /**
     * 请求相册权限
     */
    fun requestGalleryPermission(): Flow<Boolean>

    /**
     * 打开应用设置
     */
    suspend fun openAppSettings(): Boolean

    /**
     * 获取权限状态描述
     */
    fun getPermissionStatusText(): String
}