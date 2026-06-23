package com.frank.anim.ui.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.models.FireworkAnimation

/**
 * 烟花效果组件
 */
@Composable
fun FireworkEffect(clickCount: Int) {
    var fireworks by remember { mutableStateOf<List<FireworkAnimation>>(emptyList()) }
    var lastClickCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(clickCount) {
        if (clickCount > lastClickCount) {
            lastClickCount = clickCount

            val newFireworkCount = 6 + (kotlin.random.Random.nextInt(5))
            val newFireworks = (0 until newFireworkCount).map { index ->
                createRandomFirework(clickCount * 1000 + index)
            }

            // 直接添加新烟花，不删除旧烟花
            fireworks = fireworks + newFireworks
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        fireworks.forEach { firework ->
            key(firework.id) {
                SingleFireworkSimple(
                    firework = firework,
                    onComplete = {
                        fireworks = fireworks.filter { it.id != firework.id }
                    }
                )
            }
        }

        // 调试信息
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(16.dp, 16.dp)
        ) {
            Text(
                text = "烟花数量: ${fireworks.size}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "点击次数: $clickCount",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = "最新烟花: ${fireworks.takeLast(1).map { it.emoji }}",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 单个烟花组件
 */
@Composable
private fun SingleFireworkSimple(
    firework: FireworkAnimation,
    onComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(firework.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000)
        )
        onComplete()
    }

    val scale by remember {
        derivedStateOf {
            val p = progress.value
            when {
                p < 0.3f -> 0.5f + (p / 0.3f) * 1.0f
                p < 0.7f -> 1.5f
                else -> 1.5f - ((p - 0.7f) / 0.3f) * 1.0f
            }
        }
    }

    val alpha by remember {
        derivedStateOf {
            val p = progress.value
            when {
                p < 0.2f -> (p / 0.2f) * 1.0f
                p < 0.6f -> 1.0f
                else -> 1.0f - ((p - 0.6f) / 0.4f) * 1.0f
            }
        }
    }

    Text(
        text = firework.emoji,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .offset(x = firework.x.dp, y = firework.y.dp)
            .scale(scale)
            .alpha(alpha)
    )
}

/**
 * 创建随机位置的烟花数据
 */
private fun createRandomFirework(id: Int): FireworkAnimation {
    val fireworkEmojis = listOf(
        "🎆", "🎇", "✨", "💥", "🌟", "⭐", "💫", "🔥", "💎", "🎊",
        "🎉", "🎈", "🎁", "🎀", "💝", "💖", "💕", "💗", "💓", "💞"
    )

    val fireworkColors = listOf(
        Color(0xFFFF6B35), // 橙色
        Color(0xFFFFD23F), // 黄色
        Color(0xFF06FFA5), // 绿色
        Color(0xFF4FC3F7), // 蓝色
        Color(0xFF8B5CF6), // 紫色
        Color(0xFFEF4444), // 红色
        Color(0xFFFF69B4), // 粉色
        Color(0xFF00CED1)  // 青色
    )

    val screenWidth = 400f
    val screenHeight = 600f
    val margin = 50f

    // 生成随机坐标，确保在安全区域内
    val randomX = (kotlin.random.Random.nextFloat() * (screenWidth - 2 * margin) + margin)
    val randomY = (kotlin.random.Random.nextFloat() * (screenHeight - 2 * margin) + margin)

    return FireworkAnimation(
        id = id,
        x = randomX - screenWidth / 2,
        y = randomY - screenHeight / 2,
        emoji = fireworkEmojis.random(),
        color = fireworkColors.random(),
        progress = 0f,
        scale = 0.5f,
        alpha = 0f
    )
}
