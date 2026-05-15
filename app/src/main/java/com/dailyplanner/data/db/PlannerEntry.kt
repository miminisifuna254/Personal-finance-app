package com.dailyplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_entries")
data class PlannerEntry(
    @PrimaryKey val dateKey: String, // format: "YYYY-MM-DD"
    val mainFocus: String = "",
    val priority1: String = "",
    val priority2: String = "",
    val priority3: String = "",
    val priority4: String = "",
    val priority5: String = "",
    val mood: String = "", // "happy", "neutral", "sad", "frustrated"
    val notes: String = ""
)
