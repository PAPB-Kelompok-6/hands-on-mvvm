package com.kelompok6.todolistreactiveapp.model

import kotlinx.coroutines.flow.Flow

data class TodoRepository(private val dao: TodoDAO) {
    val allTodo: Flow<List<Todo>> = dao.getAll()

    suspend fun addTodo(todo: Todo) {
        dao.insert(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        dao.update(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        dao.delete(todo)
    }
}
