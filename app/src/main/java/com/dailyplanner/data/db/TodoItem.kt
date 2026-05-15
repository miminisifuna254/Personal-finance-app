package com.dailyplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateKey: String,
    val text: String = "",
    val isDone: Boolean = false,
    val sortOrder: Int = 0
)
