package com.kelompok6.todolistreactiveapp.model

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDAO) {
    val allTodo: Flow<List<Todo>> = dao.getAll()

    suspend fun addTodo(title: String) = dao.insert(Todo(title = title))
    suspend fun updateTodo(todo: Todo) = dao.update(todo)
    suspend fun deleteTodo(todo: Todo) = dao.delete(todo)
}
