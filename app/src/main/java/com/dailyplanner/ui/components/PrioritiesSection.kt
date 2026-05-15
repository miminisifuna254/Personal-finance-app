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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyplanner.ui.theme.CaveatFamily
import com.dailyplanner.ui.theme.SageGreen

@Composable
fun PrioritiesSection(
    priorities: List<String>,
    onPriorityChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val fieldColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Priorities",
            fontFamily = CaveatFamily,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 22.sp,
            color = SageGreen
        )

        priorities.forEachIndexed { index, priority ->
            PriorityRow(
                number = index + 1,
                value = priority,
                onValueChange = { onPriorityChange(index, it) },
                fieldColor = fieldColor,
                textColor = textColor
            )
        }
    }
}

@Composable
private fun PriorityRow(
    number: Int,
    value: String,
    onValueChange: (String) -> Unit,
    fieldColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$number.",
            fontFamily = CaveatFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = SageGreen,
            modifier = Modifier.width(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(fieldColor)
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontFamily = CaveatFamily,
                    fontSize = 16.sp,
                    color = textColor
                ),
                cursorBrush = SolidColor(textColor),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            "Priority $number…",
                            fontFamily = CaveatFamily,
                            fontSize = 16.sp,
                            color = textColor.copy(alpha = 0.35f)
                        )
                    }
                    inner()
                }
            )
        }
    }
}
