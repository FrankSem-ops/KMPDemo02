package com.frank.anim.ui.models

import androidx.compose.ui.graphics.Color

/**
 * 波纹效果数据类
 */
data class RippleEffect(
    val id: Int,
    val scale: Float,
    val alpha: Float
)

/**
 * 彩虹效果数据类
 */
data class RainbowEffect(
    val id: Int,
    val scale: Float,
    val alpha: Float,
    val color: Color
)

/**
 * 表情包粒子数据类
 * 
 * 用于存储表情包粒子动画的所有状态信息，包括位置、表情符号和动画进度
 */
data class EmojiParticle(
    val id: Int,                    // 粒子唯一标识符，用于区分不同的粒子
    val startX: Float,              // 粒子起始X坐标（通常为0，从中心发射）
    val startY: Float,              // 粒子起始Y坐标（通常为0，从中心发射）
    val endX: Float,                // 粒子终点X坐标（通过三角函数计算得出）
    val endY: Float,                // 粒子终点Y坐标（通过三角函数计算得出）
    val emoji: String,              // 粒子显示的表情符号（如😀、❤️等）
    val progress: Float              // 动画进度（0.0到1.0，0表示刚开始，1表示到达终点）
)

/**
 * 旋转表情包粒子数据类
 */
data class RotatingEmojiParticle(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val emoji: String,
    val progress: Float,
    val rotationSpeed: Float
)

/**
 * 烟花动画数据类
 */
data class FireworkAnimation(
    val id: Int,
    val x: Float,
    val y: Float,
    val emoji: String,
    val color: Color,
    val progress: Float,
    val scale: Float,
    val alpha: Float
)
