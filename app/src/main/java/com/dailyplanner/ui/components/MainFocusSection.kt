package com.dailyplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyplanner.ui.theme.CaveatFamily
import com.dailyplanner.ui.theme.WarmBeige
import com.dailyplanner.ui.theme.DarkBrown

@Composable
fun MainFocusSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = modifier
            .clip(ScallopedShape(scallops = 10, scallopDepth = 0.03f))
            .background(containerColor)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Main Focus",
                fontFamily = CaveatFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = textColor.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "★ ",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontFamily = CaveatFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    ),
                    cursorBrush = SolidColor(textColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(
                                "What's your main focus today?",
                                fontFamily = CaveatFamily,
                                fontSize = 18.sp,
                                color = textColor.copy(alpha = 0.4f)
                            )
                        }
                        inner()
                    }
                )
            }
            Divider(
                color = textColor.copy(alpha = 0.25f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
