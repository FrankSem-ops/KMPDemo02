package com.frank.anim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.components.LikeButton
import com.frank.anim.ui.components.AppSymbolIcon
import com.frank.anim.ui.components.AppSymbols
import com.frank.anim.ui.effects.BaseRippleEffect
import com.frank.anim.ui.effects.EmojiParticleEffect
import com.frank.anim.ui.effects.FireworkEffect
import com.frank.anim.ui.effects.RainbowLayerEffect
import com.frank.anim.ui.effects.RotatingParticleEffect

/**
 * 类似微博长按点赞的彩虹动画演示
 * 层次递进式动画效果
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikeAnimationDemo(
    onBack: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    var animationPhase by remember { mutableIntStateOf(0) }
    var clickCount by remember { mutableIntStateOf(0) }
    var isCombinedMode by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "多层动画效果",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            AppSymbolIcon(
                                symbol = AppSymbols.Back,
                                contentDescription = "返回",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD), // 浅蓝色
                            Color(0xFFBBDEFB), // 中等蓝色
                            Color(0xFF90CAF9)  // 深蓝色
                        )
                    )
                )
        ) {
            if (isPressed) {
                Box(
                    modifier = Modifier.offset(y = (-140).dp)
                ) {
                    LayeredAnimationEffects(
                        phase = animationPhase,
                        clickCount = clickCount,
                        isCombinedMode = isCombinedMode
                    )
                }
            }

            LikeButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-140).dp),
                onPressStart = {
                    isPressed = true
                    clickCount++
                }
            )

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(0.95f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "层次递进式动画演示",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "当前阶段 ${animationPhase + 1}/5 ${
                                    getEffectName(
                                        animationPhase
                                    )
                                }",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 模式切换按钮
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCombinedMode)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "模式:",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )

                            Button(
                                onClick = { isCombinedMode = !isCombinedMode },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCombinedMode)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    if (isCombinedMode) "组合模式" else "单独模式",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (animationPhase < 4) {
                                    animationPhase++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "下一层",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                animationPhase = 0
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "重置",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = getEffectDescription(animationPhase, isCombinedMode),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "点击图标触发动画效果",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * 层次递进式动画效果
 */
@Composable
fun LayeredAnimationEffects(phase: Int, clickCount: Int, isCombinedMode: Boolean) {

    if (isCombinedMode) {
        // 组合模式：叠加所有效果
        when (phase) {
            0 -> {
                // 第一阶段：基础波纹
                BaseRippleEffect(clickCount)
            }

            1 -> {
                // 第二阶段： 彩虹色彩层
                RainbowLayerEffect(clickCount)
            }

            2 -> {
                // 第三阶段： 彩虹色彩层 + 表情包粒子效果
                RainbowLayerEffect(clickCount)
                EmojiParticleEffect(clickCount)
            }

            3 -> {
                // 第四阶段： 彩虹色彩层 + 表情包粒子效果 + 旋转表情包效果
                RainbowLayerEffect(clickCount)
                EmojiParticleEffect(clickCount)
                RotatingParticleEffect(clickCount)
            }

            4 -> {
                // 第五阶段：彩虹色彩层 + 表情包粒子效果 + 旋转表情包效果 + 烟花效果
                RainbowLayerEffect(clickCount)
                EmojiParticleEffect(clickCount)
                RotatingParticleEffect(clickCount)
                FireworkEffect(clickCount)
            }
        }
    } else {
        // 单独模式：只显示当前阶段的效果
        when (phase) {
            0 -> {
                // 第一阶段：基础波纹
                BaseRippleEffect(clickCount)
            }

            1 -> {
                // 第二阶段：彩虹色彩层
                RainbowLayerEffect(clickCount)
            }

            2 -> {
                // 第三阶段：表情包粒子效果
                EmojiParticleEffect(clickCount)
            }

            3 -> {
                // 第四阶段：旋转表情包效果
                RotatingParticleEffect(clickCount)
            }

            4 -> {
                // 第五阶段：烟花效果
                FireworkEffect(clickCount)
            }
        }
    }
}

/**
 * 获取当前阶段的效果名称
 */
private fun getEffectName(phase: Int): String {
    return when (phase) {
        0 -> "基础波纹效果"
        1 -> "彩虹色彩层"
        2 -> "表情粒子效果"
        3 -> "表情包旋转效果"
        4 -> "烟花闪烁效果"
        else -> "基础波纹效果"
    }
}

/**
 * 获取当前阶段的效果描述
 */
private fun getEffectDescription(phase: Int, isCombinedMode: Boolean): String {
    return if (isCombinedMode) {
        // 组合模式描述
        when (phase) {
            0 -> "第1阶段：基础波纹扩散动画"
            1 -> "第2阶段：基础波纹 + 彩虹色彩层"
            2 -> "第3阶段：基础波纹 + 彩虹色彩层 + 表情包粒子"
            3 -> "第4阶段：基础波纹 + 彩虹色彩层 + 表情包粒子 + 旋转表情包"
            4 -> "第5阶段：所有效果叠加（完整版）"
            else -> "第1阶段：基础波纹扩散动画"
        }
    } else {
        // 单独模式描述
        when (phase) {
            0 -> "第1阶段：基础波纹扩散动画"
            1 -> "第2阶段：彩虹渐变色彩效果"
            2 -> "第3阶段：表情符号粒子爆炸"
            3 -> "第4阶段：旋转表情包效果"
            4 -> "第5阶段：烟花闪烁效果"
            else -> "第1阶段：基础波纹扩散动画"
        }
    }
}
