package com.frank.anim.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object AppSymbols {
    const val Back = "←"
    const val Info = "ℹ"
    const val Lock = "🔒"
    const val Settings = "⚙"
    const val Refresh = "↻"
    const val Search = "🔎"
    const val List = "≡"
    const val Clear = "✕"
    const val Play = "▶"
    const val Check = "✓"
    const val Star = "★"
    const val Close = "✕"
}

@Composable
fun AppSymbolIcon(
    symbol: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null,
    fontSize: TextUnit = 18.sp
) {
    val describedModifier = if (contentDescription.isNullOrBlank()) {
        modifier
    } else {
        modifier.semantics {
            this.contentDescription = contentDescription
        }
    }

    Box(
        modifier = describedModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = tint,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
