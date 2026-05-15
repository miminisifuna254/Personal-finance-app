package com.dailyplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailyplanner.data.db.PlannerDatabase
import com.dailyplanner.data.db.PlannerEntry
import com.dailyplanner.data.db.TodoItem
import com.dailyplanner.data.repository.PlannerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlannerRepository(
        PlannerDatabase.getInstance(application).plannerDao()
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    val currentDateKey: StateFlow<String> = _currentDate
        .map { it.format(dateFormatter) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now().format(dateFormatter))

    val entry: StateFlow<PlannerEntry?> = currentDateKey
        .flatMapLatest { key -> repository.getEntry(key) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val todos: StateFlow<List<TodoItem>> = currentDateKey
        .flatMapLatest { key -> repository.getTodos(key) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val completedCount: StateFlow<Int> = todos
        .map { list -> list.count { it.isDone } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun goToPreviousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
    }

    fun goToNextDay() {
        _currentDate.value = _currentDate.value.plusDays(1)
    }

    fun goToToday() {
        _currentDate.value = LocalDate.now()
    }

    fun setDate(date: LocalDate) {
        _currentDate.value = date
    }

    private fun currentEntry(): PlannerEntry =
        entry.value ?: PlannerEntry(dateKey = currentDateKey.value)

    fun updateMainFocus(text: String) = viewModelScope.launch {
        repository.upsertEntry(currentEntry().copy(mainFocus = text))
    }

    fun updatePriority(index: Int, text: String) = viewModelScope.launch {
        val e = currentEntry()
        val updated = when (index) {
            0 -> e.copy(priority1 = text)
            1 -> e.copy(priority2 = text)
            2 -> e.copy(priority3 = text)
            3 -> e.copy(priority4 = text)
            4 -> e.copy(priority5 = text)
            else -> e
        }
        repository.upsertEntry(updated)
    }

    fun updateMood(mood: String) = viewModelScope.launch {
        val e = currentEntry()
        val newMood = if (e.mood == mood) "" else mood
        repository.upsertEntry(e.copy(mood = newMood))
    }

    fun updateNotes(text: String) = viewModelScope.launch {
        repository.upsertEntry(currentEntry().copy(notes = text))
    }

    fun addTodo() = viewModelScope.launch {
        val order = (todos.value.maxOfOrNull { it.sortOrder } ?: -1) + 1
        repository.upsertTodo(
            TodoItem(dateKey = currentDateKey.value, sortOrder = order)
        )
    }

    fun updateTodoText(item: TodoItem, text: String) = viewModelScope.launch {
        repository.upsertTodo(item.copy(text = text))
    }

    fun toggleTodo(item: TodoItem) = viewModelScope.launch {
        repository.upsertTodo(item.copy(isDone = !item.isDone))
    }

    fun deleteTodo(item: TodoItem) = viewModelScope.launch {
        repository.deleteTodo(item)
    }
}
