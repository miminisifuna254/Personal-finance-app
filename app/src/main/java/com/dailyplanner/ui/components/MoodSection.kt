package com.dailyplanner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyplanner.ui.theme.CaveatFamily
import com.dailyplanner.ui.theme.SageGreen

data class MoodOption(val key: String, val emoji: String, val label: String)

val moodOptions = listOf(
    MoodOption("happy", "😊", "Happy"),
    MoodOption("neutral", "😐", "Neutral"),
    MoodOption("sad", "😔", "Sad"),
    MoodOption("frustrated", "😠", "Angry")
)

@Composable
fun MoodSection(
    selectedMood: String,
    onMoodSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.primary
    val onBgColor = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Mood",
            fontFamily = CaveatFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = onBgColor
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            moodOptions.forEach { mood ->
                MoodButton(
                    mood = mood,
                    isSelected = selectedMood == mood.key,
                    onSelect = { onMoodSelect(mood.key) }
                )
            }
        }
    }
}

@Composable
private fun MoodButton(
    mood: MoodOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "mood_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color.White.copy(alpha = 0.3f)
                    else Color.White.copy(alpha = 0.1f)
                )
                .then(
                    if (isSelected) Modifier.border(2.dp, Color.White, CircleShape)
                    else Modifier
                )
        ) {
            Text(mood.emoji, fontSize = 24.sp)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            mood.label,
            fontFamily = CaveatFamily,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
