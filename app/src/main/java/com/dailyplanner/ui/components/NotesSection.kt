package com.dailyplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyplanner.ui.theme.CaveatFamily

@Composable
fun NotesSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.secondary
    val onBgColor = MaterialTheme.colorScheme.onSecondary

    Box(
        modifier = modifier
            .clip(ScallopedShape(scallops = 12, scallopDepth = 0.025f))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Notes",
                fontFamily = CaveatFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = onBgColor.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontFamily = CaveatFamily,
                    fontSize = 16.sp,
                    color = onBgColor,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(onBgColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            "Jot down anything on your mind…",
                            fontFamily = CaveatFamily,
                            fontSize = 16.sp,
                            color = onBgColor.copy(alpha = 0.4f)
                        )
                    }
                    inner()
                }
            )
        }
    }
}
