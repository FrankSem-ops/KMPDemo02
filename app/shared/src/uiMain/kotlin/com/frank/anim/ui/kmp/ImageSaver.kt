package com.frank.anim.ui.kmp

import androidx.compose.runtime.Composable

/**
 * 图片保存接口（平台实现）
 */
expect class ImageSaver {
    /**
     * 保存图片到本地
     * @param imageBytes 图片字节数组
     * @param fileName 文件名（不含扩展名）
     * @return 保存成功返回文件路径，失败返回null
     */
    suspend fun saveImage(imageBytes: ByteArray, fileName: String): String?
}

/**
 * 创建ImageSaver实例（平台实现）
 */
@Composable
expect fun rememberImageSaver(): ImageSaver

