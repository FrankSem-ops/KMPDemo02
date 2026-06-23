package com.frank.anim.ui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.models.EmojiParticle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * 表情包粒子层效果 - 第三阶段
 */
@Composable
fun EmojiParticleEffect(clickCount: Int) {
    var allParticles by remember { mutableStateOf<List<EmojiParticle>>(emptyList()) }
    
    var lastClickCount by remember { mutableIntStateOf(0) }
    
    val coroutineScope = rememberCoroutineScope()
    

    fun emitNewBatch() {
        val currentBatchId = clickCount
        
        val newParticles = createEmojiParticles().map { particle ->
            particle.copy(id = particle.id + currentBatchId * 100)
        }
        
        allParticles = allParticles + newParticles
        
        coroutineScope.launch {
            var batchProgress = 0f
            
            repeat(60) { step ->
                batchProgress = step / 60f
                
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
            
            delay(200L)
            
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
        // 遍历所有粒子并绘制
        allParticles.forEach { particle ->
            val progress = particle.progress
            
            // 计算粒子的当前位置（线性插值）
            val x = particle.startX + (particle.endX - particle.startX) * progress
            val y = particle.startY + (particle.endY - particle.startY) * progress
            
            // 抛物线效果 - 增加重力效果
            val gravityY = particle.startY + (particle.endY - particle.startY) * progress + 
                          (progress * progress) * 200f  // 重力系数200f
            
            // 粒子在飞行过程中逐渐缩小
            val scale = 1f - progress * 0.2f  // 最终缩小到80%
            
            // 只绘制未到达终点的粒子
            if (progress < 1f) {
                Box(
                    modifier = Modifier
                        .offset(x = x.dp, y = gravityY.dp)
                        .scale(scale)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Text(
                        text = particle.emoji,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

/**
 * 创建表情包粒子
 */
private fun createEmojiParticles(): List<EmojiParticle> {
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
    
    return (0 until 18).map { index -> // 表情包数量18
        val randomAngle = (kotlin.random.Random.nextFloat() * 360f) * (PI / 180f) // 随机角度 角度转弧度公式：弧度 = 角度 × (π / 180)
        val randomDistance = 150f + (kotlin.random.Random.nextFloat() * 200f) // 增加发射距离，在150f距离的基础上加上0到200的随机数，确保位置正确
        val endX = cos(randomAngle).toFloat() * randomDistance  // 计算粒子终点的X坐标（水平方向）
        val endY = sin(randomAngle).toFloat() * randomDistance  // 计算粒子终点的Y坐标（垂直方向）
        
        val randomEmoji = emojis.random() // 随机表情包
        
        EmojiParticle(
            id = index,
            startX = 0f,
            startY = 0f,
            endX = endX,
            endY = endY,
            emoji = randomEmoji,
            progress = 0f
        )
    }
}