package com.sem.kmp01.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElementLibraryDialog(
    doNotRemindAgain: Boolean,
    onDoNotRemindAgainChange: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DialogHeaderIcon()
            DialogTitle()
            DialogDescription()
            DoNotRemindAgainRow(
                checked = doNotRemindAgain,
                onToggle = onDoNotRemindAgainChange,
            )
            ConfirmButton(onClick = onConfirm)
            DismissButton(onClick = onDismiss)
        }
    }
}

@Composable
private fun DialogHeaderIcon() {
    Box(
        modifier = Modifier
            .size(74.dp)
            .background(color = Color(0xFF161A1D), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        FolderGlyph()
    }
}

@Composable
private fun DialogTitle() {
    Text(
        text = "常用素材存入元素库",
        modifier = Modifier.padding(top = 22.dp),
        color = Color(0xFF111111),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun DialogDescription() {
    Text(
        text = "发现您已多次使用该本次素材，是否将其存入资产库？存入后可多端同步，随时调用。",
        modifier = Modifier.padding(top = 20.dp),
        color = Color(0xFF6F7682),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun DoNotRemindAgainRow(
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        SelectionIndicator(checked = checked)
        Text(
            text = "不再提醒",
            modifier = Modifier.padding(start = 8.dp),
            color = Color(0xFFA1A7B3),
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun ConfirmButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 22.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF12181C),
            contentColor = Color.White,
        ),
    ) {
        Text(
            text = "存入并使用",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DismissButton(onClick: () -> Unit) {
    Text(
        text = "仅使用",
        modifier = Modifier
            .padding(top = 18.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = Color(0xFF737A86),
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun FolderGlyph() {
    Column(
        modifier = Modifier.width(34.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .padding(start = 3.dp)
                .width(14.dp)
                .height(7.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
                )
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
                ),
        )
        Box(
            modifier = Modifier
                .offset(y = (-1).dp)
                .width(30.dp)
                .height(20.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                )
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(6.dp),
                ),
        )
    }
}

@Composable
private fun SelectionIndicator(checked: Boolean) {
    val shape: Shape = CircleShape
    Box(
        modifier = Modifier
            .size(18.dp)
            .background(
                color = if (checked) Color(0xFFD2D6DE) else Color.Transparent,
                shape = shape,
            )
            .border(
                width = 1.5.dp,
                color = Color(0xFFB6BBC6),
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Spacer(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color(0xFF7F8794), CircleShape),
            )
        }
    }
}
