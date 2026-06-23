package com.frank.anim.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frank.anim.ui.components.AppSymbolIcon
import com.frank.anim.ui.components.AppSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigateBack: () -> Unit,
    onNavigateToDemo: () -> Unit,
    onNavigateToAnimatedVisibility: () -> Unit,
    onNavigateToAnimatedContent: () -> Unit,
    onNavigateToAnimateAsState: () -> Unit,
    onNavigateToAnimatable: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Compose 动画实验",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "选择要测试的动画效果",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp) 
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNavigateToDemo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp) 
                ) {
                    Text(
                        text = "多层动画效果",
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onNavigateToAnimatedVisibility,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp) 
                ) {
                    Text(
                        text = "AnimatedVisibility 测试",
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onNavigateToAnimatedContent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(10.dp) 
                ) {
                    Text(
                        text = "AnimatedContent 测试",
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onNavigateToAnimateAsState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp) 
                ) {
                    Text(
                        text = "animate*AsState 测试",
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onNavigateToAnimatable,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp) 
                ) {
                    Text(
                        text = "Animatable - 手动控制动画",
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp) 
            ) {
                Column(
                    modifier = Modifier.padding(14.dp) 
                ) {
                    Text(
                        text = "使用说明",
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp) 
                    )
                    Text(
                        text = "• 多层动画效果：展示复杂的组合动画\n• AnimatedVisibility测试：测试各种显示/隐藏动画\n• AnimatedContent测试：测试内容切换动画效果\n• animate*AsState测试：测试状态动画效果\n• Animatable测试：手动控制动画进度和方向",
                        fontSize = 12.sp, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp 
                    )
                }
            }
        }
    }
}
