package com.frank.anim.ui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.models.RotatingEmojiParticle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * 旋转表情包粒子效果组件
 */
@Composable
fun RotatingParticleEffect(clickCount: Int) {
    var allParticles by remember { mutableStateOf<List<RotatingEmojiParticle>>(emptyList()) }
    var lastClickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    fun emitNewBatch() {
        val currentBatchId = clickCount
        val newParticles = createRotatingEmojiParticles().map { particle ->
            particle.copy(id = particle.id + currentBatchId * 100)
        }
        allParticles = allParticles + newParticles
        
        coroutineScope.launch {
            var batchProgress: Float

            repeat(120) { step ->
                batchProgress = step / 120f
                allParticles = allParticles.map { particle ->
                    if (particle.id >= currentBatchId * 100 && particle.id < (currentBatchId + 1) * 100) {
                        particle.copy(progress = batchProgress)
                    } else {
                        particle
                    }
                }
                delay(12L)
            }
            
            batchProgress = 1f
            allParticles = allParticles.map { particle ->
                if (particle.id >= currentBatchId * 100 && particle.id < (currentBatchId + 1) * 100) {
                    particle.copy(progress = batchProgress)
                } else {
                    particle
                }
            }
            
            delay(500L)
            
            allParticles = allParticles.filter {
                it.id < currentBatchId * 100 || it.id >= (currentBatchId + 1) * 100 
            }
        }
    }
    
    LaunchedEffect(clickCount) {
        if (clickCount > lastClickCount) {
            lastClickCount = clickCount
            emitNewBatch()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        allParticles.forEach { particle ->
            val progress = particle.progress
            val x = particle.startX + (particle.endX - particle.startX) * progress

            val gravityY = particle.startY + (particle.endY - particle.startY) * progress +
                          (progress * progress) * 400f
            
            if (progress < 1f) {
                val rotation = progress * 1080f * particle.rotationSpeed
                val scale = 1f - progress * 0.3f
                
                Box(
                    modifier = Modifier
                        .offset(x = x.dp, y = gravityY.dp)
                        .scale(scale)
                        .rotate(rotation)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape // 圆形背景
                        )
                        .padding(4.dp) // 内边距
                ) {
                    // 表情符号文本
                    Text(
                        text = particle.emoji,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * 创建旋转表情包粒子数据
 * 
 * 生成10个粒子，每个粒子都有随机的方向、距离和表情符号
 * 使用三角函数计算粒子的终点坐标，实现圆形分布
 * 
 * @return List<RotatingEmojiParticle> 粒子数据列表
 */
private fun createRotatingEmojiParticles(): List<RotatingEmojiParticle> {
    val emojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
        "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
        "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
        "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤥",
        "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮",
        "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "😎", "🤓",
        "🧐", "😕", "😟", "🙁", "☹️", "😮", "😯", "😲", "😳", "🥺",
        "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣",
        "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈",
        "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾",
        "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"
    )
    
    return (0 until 10).map { index ->
        val randomAngle = (kotlin.random.Random.nextFloat() * 360f) * (PI / 180f)
        val randomDistance = 200f + (kotlin.random.Random.nextFloat() * 300f)
        
        val endX = cos(randomAngle).toFloat() * randomDistance
        val endY = sin(randomAngle).toFloat() * randomDistance
        
        val randomEmoji = emojis.random()
        
        val baseRotationSpeed = (kotlin.random.Random.nextFloat() * 4.0f) - 2.0f
        val distanceMultiplier = 0.5f + (randomDistance / 500f) * 1.5f
        val randomRotationSpeed = baseRotationSpeed * distanceMultiplier
        
        RotatingEmojiParticle(
            id = index,
            startX = 0f,
            startY = 0f,
            endX = endX,
            endY = endY,
            emoji = randomEmoji,
            progress = 0f,
            rotationSpeed = randomRotationSpeed
        )
    }
}