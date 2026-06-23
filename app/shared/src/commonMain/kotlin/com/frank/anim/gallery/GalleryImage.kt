package com.frank.anim.gallery

/**
 * 相册图片数据模型
 */
data class GalleryImage(
    val id: String,
    val name: String,
    val uri: String,
    val path: String,
    val size: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val dateAdded: Long,
    val dateModified: Long,
    val orientation: Int = 0,
    val isVideo: Boolean = false,
    val duration: Long? = null
) {
    fun getDisplaySize(): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> "${(mb * 10).toInt() / 10.0}MB" // 保留一位小数
            kb >= 1.0 -> "${kb.toInt()}KB"
            else -> "${size}B"
        }
    }

    fun getDimensions(): String = "${width} × ${height}"

    fun getAspectRatio(): Float = width.toFloat() / height.toFloat()

    fun isLandscape(): Boolean = width > height
    fun isPortrait(): Boolean = height > width
    fun isSquare(): Boolean = width == height

    fun getFileExtension(): String {
        return name.substringAfterLast('.', "").lowercase()
    }

    fun isImage(): Boolean = !isVideo && (mimeType.startsWith("image/") ||
                                          getFileExtension() in setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif"))

    fun isVideoType(): Boolean = isVideo || (mimeType.startsWith("video/") ||
                                          getFileExtension() in setOf("mp4", "mov", "mkv", "avi", "wmv", "flv", "webm"))
}

/**
 * 相册文件夹信息
 */
data class GalleryAlbum(
    val id: String,
    val name: String,
    val coverImage: GalleryImage?,
    val imageCount: Int,
    val dateCreated: Long
)
