package com.dailyplanner.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {
    @Query("SELECT * FROM planner_entries WHERE dateKey = :dateKey")
    fun getEntry(dateKey: String): Flow<PlannerEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: PlannerEntry)

    @Query("SELECT * FROM todo_items WHERE dateKey = :dateKey ORDER BY sortOrder ASC, id ASC")
    fun getTodos(dateKey: String): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTodo(item: TodoItem)

    @Delete
    suspend fun deleteTodo(item: TodoItem)

    @Query("DELETE FROM todo_items WHERE dateKey = :dateKey")
    suspend fun deleteTodosForDate(dateKey: String)
}
