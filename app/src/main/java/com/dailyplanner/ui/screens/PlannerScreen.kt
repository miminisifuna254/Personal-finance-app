package com.dailyplanner.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailyplanner.data.db.PlannerEntry
import com.dailyplanner.ui.components.*
import com.dailyplanner.viewmodel.PlannerViewModel
import kotlin.math.abs

@Composable
fun PlannerScreen(viewModel: PlannerViewModel) {
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val entry by viewModel.entry.collectAsStateWithLifecycle()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val completedCount by viewModel.completedCount.collectAsStateWithLifecycle()

    val priorities = listOf(
        entry?.priority1 ?: "",
        entry?.priority2 ?: "",
        entry?.priority3 ?: "",
        entry?.priority4 ?: "",
        entry?.priority5 ?: ""
    )

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val SWIPE_THRESHOLD = 80f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset > SWIPE_THRESHOLD) viewModel.goToPreviousDay()
                        else if (dragOffset < -SWIPE_THRESHOLD) viewModel.goToNextDay()
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { _, delta -> dragOffset += delta }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlannerTopBar(
                currentDate = currentDate,
                completedCount = completedCount,
                totalCount = todos.size,
                onPrevDay = viewModel::goToPreviousDay,
                onNextDay = viewModel::goToNextDay,
                onTodayClick = viewModel::goToToday,
                onDateSelected = viewModel::setDate
            )

            Box(modifier = Modifier.fillMaxSize()) {
                // Graph paper background on the scroll area
                GraphPaperBackground(
                    lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Row 1: Main Focus (full width)
                    MainFocusSection(
                        value = entry?.mainFocus ?: "",
                        onValueChange = viewModel::updateMainFocus,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Row 2: Priorities | Mood stacked
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrioritiesSection(
                            priorities = priorities,
                            onPriorityChange = viewModel::updatePriority,
                            modifier = Modifier.weight(1f)
                        )

                        Column(
                            modifier = Modifier.width(160.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MoodSection(
                                selectedMood = entry?.mood ?: "",
                                onMoodSelect = viewModel::updateMood,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Decorative elements
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LeafDecor(size = 36.dp)
                                StarburstIcon(size = 32.dp)
                                PetalDecor(size = 28.dp)
                            }
                        }
                    }

                    // Row 3: To-do list (full width)
                    TodoSection(
                        todos = todos,
                        onToggle = viewModel::toggleTodo,
                        onTextChange = viewModel::updateTodoText,
                        onDelete = viewModel::deleteTodo,
                        onAddItem = viewModel::addTodo,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Row 4: Notes (full width)
                    NotesSection(
                        value = entry?.notes ?: "",
                        onValueChange = viewModel::updateNotes,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bottom spacing
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
