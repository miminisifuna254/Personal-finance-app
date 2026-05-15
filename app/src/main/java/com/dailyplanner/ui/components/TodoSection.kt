package com.dailyplanner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyplanner.data.db.TodoItem
import com.dailyplanner.ui.theme.CaveatFamily
import com.dailyplanner.ui.theme.SageGreen

@Composable
fun TodoSection(
    todos: List<TodoItem>,
    onToggle: (TodoItem) -> Unit,
    onTextChange: (TodoItem, String) -> Unit,
    onDelete: (TodoItem) -> Unit,
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "To-do List",
            fontFamily = CaveatFamily,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        todos.forEach { item ->
            TodoRow(
                item = item,
                onToggle = { onToggle(item) },
                onTextChange = { onTextChange(item, it) },
                onDelete = { onDelete(item) },
                textColor = textColor
            )
        }

        if (todos.size < 10 || todos.isNotEmpty()) {
            TextButton(
                onClick = onAddItem,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.Add, contentDescription = null,
                    tint = SageGreen, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "Add item",
                    fontFamily = CaveatFamily,
                    fontSize = 16.sp,
                    color = SageGreen
                )
            }
        }
    }
}

@Composable
private fun TodoRow(
    item: TodoItem,
    onToggle: () -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color
) {
    val checkBgColor by animateColorAsState(
        targetValue = if (item.isDone) SageGreen else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label = "check_bg"
    )
    val checkBorderColor by animateColorAsState(
        targetValue = if (item.isDone) SageGreen else MaterialTheme.colorScheme.outline,
        animationSpec = tween(200),
        label = "check_border"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Checkbox
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(checkBgColor)
                .border(1.5.dp, checkBorderColor, CircleShape)
                .clickable(onClick = onToggle)
        ) {
            if (item.isDone) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Text field
        BasicTextField(
            value = item.text,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                fontFamily = CaveatFamily,
                fontSize = 16.sp,
                color = if (item.isDone) textColor.copy(alpha = 0.45f) else textColor,
                textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
            ),
            cursorBrush = SolidColor(textColor),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (item.text.isEmpty()) {
                    Text(
                        "Task…",
                        fontFamily = CaveatFamily,
                        fontSize = 16.sp,
                        color = textColor.copy(alpha = 0.3f)
                    )
                }
                inner()
            }
        )

        // Delete
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete",
                tint = textColor.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
