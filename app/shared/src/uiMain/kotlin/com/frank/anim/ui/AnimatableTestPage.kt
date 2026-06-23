package com.frank.anim.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.components.AppSymbolIcon
import com.frank.anim.ui.components.AppSymbols
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatableTestPage(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Animatable 测试",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppSymbolIcon(
                            symbol = AppSymbols.Back,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Animatable 动画演示",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "使用Animatable实现对动画的精确手动控制",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Animatable 使用说明",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "• 手动控制：可以精确控制动画的进度和方向\n• 可中断：随时可以停止、暂停、恢复动画\n• 可组合：多个Animatable可以组合使用\n• 高性能：基于协程，性能优异",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            // 示例1：手动控制位置动画
            ManualPositionControl()

            // 示例2：可中断的旋转动画
            InterruptibleRotationAnimation()

            // 示例3：组合动画控制
            CombinedAnimationControl()

            // 示例4：拖拽控制动画
            DragControlledAnimation()

            // 底部空白间距，确保可以看到底部导航栏
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// 示例1：手动控制位置动画
@Composable
fun ManualPositionControl() {
    val scope = rememberCoroutineScope()
    val positionX = remember { Animatable(0f) }
    val positionY = remember { Animatable(0f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "手动控制位置动画",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .offset(
                            x = positionX.value.dp,
                            y = positionY.value.dp
                        )
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            positionX.animateTo(200f, spring())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("右移")
                }
                Button(
                    onClick = {
                        scope.launch {
                            positionX.animateTo(0f, spring())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("左移")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            positionY.animateTo(60f, spring())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("下移")
                }
                Button(
                    onClick = {
                        scope.launch {
                            positionY.animateTo(0f, spring())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("上移")
                }
            }
        }
    }
}

// 示例2：可中断的旋转动画
@Composable
fun InterruptibleRotationAnimation() {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "可中断的旋转动画",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(rotation.value)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(8.dp)
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            animationJob = scope.launch {
                                while (isAnimating) {
                                    rotation.animateTo(
                                        rotation.value + 360f,
                                        tween(1000, easing = LinearEasing)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isAnimating
                ) {
                    Text("开始")
                }

                Button(
                    onClick = {
                        isAnimating = false
                        animationJob?.cancel()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isAnimating
                ) {
                    Text("暂停")
                }

                Button(
                    onClick = {
                        isAnimating = false
                        animationJob?.cancel()
                        scope.launch {
                            rotation.snapTo(0f)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("停止")
                }
            }
        }
    }
}

// 示例3：组合动画控制
@Composable
fun CombinedAnimationControl() {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "组合动画控制",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale.value)
                        .rotate(rotation.value)
                        .graphicsLayer { this.alpha = alpha.value }
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(12.dp)
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            launch { scale.animateTo(1.5f, spring()) }
                            launch { rotation.animateTo(180f, spring()) }
                            launch { alpha.animateTo(0.5f, spring()) }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("放大+旋转+透明")
                }

                Button(
                    onClick = {
                        scope.launch {
                            launch { scale.animateTo(1f, spring()) }
                            launch { rotation.animateTo(0f, spring()) }
                            launch { alpha.animateTo(1f, spring()) }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("重置")
                }
            }
        }
    }
}

// 示例4：拖拽控制动画
@Composable
fun DragControlledAnimation() {
    val scope = rememberCoroutineScope()
    val positionX = remember { Animatable(0f) }
    val positionY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "拖拽控制动画",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "拖拽移动，双击放大",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(
                            x = positionX.value.dp,
                            y = positionY.value.dp
                        )
                        .scale(scale.value)
                        .background(
                            MaterialTheme.colorScheme.error,
                            CircleShape
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { },
                                onDragEnd = { },
                                onDrag = { change, dragAmount ->
                                    scope.launch {
                                        positionX.snapTo(positionX.value + dragAmount.x)
                                        positionY.snapTo(positionY.value + dragAmount.y)
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    scope.launch {
                                        scale.animateTo(
                                            if (scale.value > 1f) 1f else 1.5f,
                                            spring()
                                        )
                                    }
                                }
                            )
                        }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            scale.animateTo(if (scale.value > 1f) 1f else 1.5f, spring())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("切换大小")
                }

                Button(
                    onClick = {
                        scope.launch {
                            positionX.snapTo(0f)
                            positionY.snapTo(0f)
                            scale.snapTo(1f)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("重置位置")
                }
            }
        }
    }
}
