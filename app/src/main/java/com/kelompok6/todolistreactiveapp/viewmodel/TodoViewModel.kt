package com.kelompok6.todolistreactiveapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok6.todolistreactiveapp.model.Todo
import com.kelompok6.todolistreactiveapp.model.TodoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(private val repo: TodoRepository) : ViewModel() {

    enum class TodoFilter { All, Active, Completed }

    private val _filter = MutableStateFlow(TodoFilter.All)
    val filter: StateFlow<TodoFilter> = _filter.asStateFlow()

    val todos: StateFlow<List<Todo>> =
        repo.allTodo.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setFilter(f: TodoFilter) { _filter.value = f }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repo.addTodo(title.trim())
        }
    }

    fun toggleTask(todo: Todo) {
        viewModelScope.launch {
            repo.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    fun deleteTask(todo: Todo) {
        viewModelScope.launch {
            repo.deleteTodo(todo)
        }
    }
}
