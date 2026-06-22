package com.sem.kmp01.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sem.kmp01.component.ElementLibraryDialog

@Composable
fun ElementLibraryDialogScreen() {
    var doNotRemindAgain by remember { mutableStateOf(false) }
    var latestActionText by remember { mutableStateOf("还没有执行任何操作") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121519))
            .safeContentPadding()
            .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        ScreenHint(
            latestActionText = latestActionText,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        ElementLibraryDialog(
            doNotRemindAgain = doNotRemindAgain,
            onDoNotRemindAgainChange = {
                doNotRemindAgain = !doNotRemindAgain
            },
            onConfirm = {
                latestActionText = if (doNotRemindAgain) {
                    "已选择“存入并使用”，并勾选了不再提醒"
                } else {
                    "已选择“存入并使用”"
                }
            },
            onDismiss = {
                latestActionText = if (doNotRemindAgain) {
                    "已选择“仅使用”，并勾选了不再提醒"
                } else {
                    "已选择“仅使用”"
                }
            },
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ScreenHint(
    latestActionText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Spec Kit 工作流演示：元素库引导弹窗",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = latestActionText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}
