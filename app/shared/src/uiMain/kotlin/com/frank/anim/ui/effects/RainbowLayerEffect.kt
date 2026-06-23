package com.frank.anim.ui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.frank.anim.ui.models.RainbowEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 彩虹层效果 - 第二阶段
 */
@Composable
fun RainbowLayerEffect(clickCount: Int) {
    var allRainbows by remember { mutableStateOf<List<RainbowEffect>>(emptyList()) }
    
    var lastClickCount by remember { mutableIntStateOf(0) }
    
    val coroutineScope = rememberCoroutineScope()

    val colors = listOf(
        Color(0xFFFF6B35), // 橙色
        Color(0xFFFFD23F), // 黄色
        Color(0xFF06FFA5), // 绿色
        Color(0xFF4FC3F7), // 蓝色
        Color(0xFF8B5CF6), // 紫色
        Color(0xFFEF4444)  // 红色
    )
    
    /**
     * 发射新的彩虹效果
     * 创建一个新的彩虹层并启动其动画
     */
    fun emitNewRainbow() {
        val currentRainbowId = clickCount
        
        val selectedColor = colors[currentRainbowId % colors.size]
        
        val newRainbow = RainbowEffect(
            id = currentRainbowId,
            scale = 1f,
            alpha = 0.4f,
            color = selectedColor
        )
        
        allRainbows = allRainbows + newRainbow
        
        coroutineScope.launch {
            var rainbowScale: Float
            var rainbowAlpha: Float
            
            repeat(150) { step ->
                val progress = step / 150f
                
                rainbowScale = 1f + progress * 1.8f
                
                rainbowAlpha = 0.4f - progress * 0.4f
                
                allRainbows = allRainbows.map { rainbow ->
                    if (rainbow.id == currentRainbowId) {
                        rainbow.copy(scale = rainbowScale, alpha = rainbowAlpha)
                    } else {
                        rainbow
                    }
                }
                
                delay(16L)
            }
            
            allRainbows = allRainbows.filter { it.id != currentRainbowId }
        }
    }
    
    LaunchedEffect(clickCount) {
        if (clickCount > lastClickCount) {
            lastClickCount = clickCount
            emitNewRainbow()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        allRainbows.forEach { rainbow ->
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(rainbow.scale)
                    .clip(CircleShape)
                    .background(
                        rainbow.color.copy(alpha = rainbow.alpha)
                    )
            )
        }
    }
}