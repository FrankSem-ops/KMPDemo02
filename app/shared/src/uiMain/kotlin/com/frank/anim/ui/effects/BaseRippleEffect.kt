package com.frank.anim.ui.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.frank.anim.ui.models.RippleEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 基础波纹效果 - 第一阶段
 */
@Composable
fun BaseRippleEffect(clickCount: Int) {

    var allRipples by remember { mutableStateOf<List<RippleEffect>>(emptyList()) }
    var lastClickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    /**
     * 发射新的波纹效果
     */
    fun emitNewRipple() {
        val currentRippleId = clickCount

        val newRipple = RippleEffect(
            id = currentRippleId,
            scale = 1f,
            alpha = 0.6f
        )

        allRipples = allRipples + newRipple

        coroutineScope.launch {
            var rippleScale: Float
            var rippleAlpha: Float

            repeat(180) { step ->
                val progress = step / 180f
                rippleScale = 1f + progress * 1.5f
                rippleAlpha = 0.6f - progress * 0.6f
                allRipples = allRipples.map { ripple ->
                    if (ripple.id == currentRippleId) {
                        ripple.copy(scale = rippleScale, alpha = rippleAlpha)
                    } else {
                        ripple
                    }
                }

                delay(16L)
            }

            allRipples = allRipples.filter { it.id != currentRippleId }
        }
    }

    LaunchedEffect(clickCount) {
        if (clickCount > lastClickCount) {
            lastClickCount = clickCount
            emitNewRipple()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        allRipples.forEach { ripple ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(ripple.scale)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFFF8C42).copy(alpha = ripple.alpha)
                    )
            )
        }
    }
}