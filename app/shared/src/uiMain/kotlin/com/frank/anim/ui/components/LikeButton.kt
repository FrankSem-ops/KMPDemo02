package com.frank.anim.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 点赞按钮组件
 */
@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    onPressStart: () -> Unit
) {
    var clickCount by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) {
            1.3f
        } else {
            1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .size(50.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF59D).copy(alpha = 0.9f), // 淡黄色中心
                        Color(0xFFFFF176).copy(alpha = 0.8f), // 中等淡黄色
                        Color(0xFFFFEB3B).copy(alpha = 0.9f)  // 淡黄色外圈
                    )
                )
            )
            .clickable {
                clickCount++
                onPressStart()

                coroutineScope.launch {
                    isAnimating = true
                    delay(300L)
                    isAnimating = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "❤️",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}