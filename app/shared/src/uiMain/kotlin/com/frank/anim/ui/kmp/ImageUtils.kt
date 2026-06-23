package com.frank.anim.ui.kmp

/**
 * 获取图片尺寸（平台实现）
 * @return Pair<宽度, 高度>，如果无法获取则返回null
 */
expect fun getImageSize(imageBytes: ByteArray): Pair<Int, Int>?


