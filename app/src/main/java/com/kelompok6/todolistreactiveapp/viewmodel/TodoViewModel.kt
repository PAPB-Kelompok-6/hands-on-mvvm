package com.kelompok6.todolistreactiveapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok6.todolistreactiveapp.model.Todo
import com.kelompok6.todolistreactiveapp.model.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TodoViewModel(private val repos: TodoRepository) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    // New: filter state
    enum class TodoFilter { All, Active, Completed }

    private val _filter = MutableStateFlow(TodoFilter.All)
    val filter: StateFlow<TodoFilter> = _filter

    init {
        viewModelScope.launch {
            repos.allTodo.collectLatest { list ->
                _todos.value = list
            }
        }
    }

    fun setFilter(f: TodoFilter) {
        _filter.value = f
    }

    fun addTask(judul: String) {
        viewModelScope.launch {
            val todo = Todo(title = judul)
            repos.addTodo(todo)
        }
    }
    fun toggleTask(todo: Todo) {
        viewModelScope.launch {
            repos.updateTodo(todo)
        }
    }
    fun deleteTask(todo: Todo) {
        viewModelScope.launch {
            repos.deleteTodo(todo)
        }
    }
}
