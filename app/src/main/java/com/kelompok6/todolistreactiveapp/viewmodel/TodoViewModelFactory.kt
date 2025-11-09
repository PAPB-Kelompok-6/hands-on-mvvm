package com.kelompok6.todolistreactiveapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kelompok6.todolistreactiveapp.model.TodoRepository

class TodoViewModelFactory(private val repo: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}