package com.dailyplanner.data.repository

import com.dailyplanner.data.db.PlannerDao
import com.dailyplanner.data.db.PlannerEntry
import com.dailyplanner.data.db.TodoItem
import kotlinx.coroutines.flow.Flow

class PlannerRepository(private val dao: PlannerDao) {

    fun getEntry(dateKey: String): Flow<PlannerEntry?> = dao.getEntry(dateKey)

    suspend fun upsertEntry(entry: PlannerEntry) = dao.upsertEntry(entry)

    fun getTodos(dateKey: String): Flow<List<TodoItem>> = dao.getTodos(dateKey)

    suspend fun upsertTodo(item: TodoItem) = dao.upsertTodo(item)

    suspend fun deleteTodo(item: TodoItem) = dao.deleteTodo(item)
}
