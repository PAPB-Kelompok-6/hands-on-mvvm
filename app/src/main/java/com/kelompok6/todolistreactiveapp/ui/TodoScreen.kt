package com.kelompok6.todolistreactiveapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok6.todolistreactiveapp.model.TodoDatabase
import com.kelompok6.todolistreactiveapp.model.TodoRepository
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModel.TodoFilter
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModelFactory


@Composable
fun TodoScreen() {
    // Inisialisasi Room Database, Repository, dan ViewModel
    val context = LocalContext.current
    val database = remember { TodoDatabase.getDatabase(context) }
    val repository = remember { TodoRepository(database.todoDao()) }
    val factory = remember { TodoViewModelFactory(repository) }

    // pindah vm jadi di sini
    val vm: TodoViewModel = viewModel(factory = factory)

    val todos by vm.todos.collectAsState()
    val filter by vm.filter.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }

    val totalCount = todos.size
    val activeCount = todos.count { !it.isDone }
    val completedCount = todos.count { it.isDone }

    // compute filtered todos based on selected filter and search query
    val filteredTodos = remember(todos, filter, query) {
        val byStatus = when (filter) {
            TodoFilter.All -> todos
            TodoFilter.Active -> todos.filter { !it.isDone }
            TodoFilter.Completed -> todos.filter { it.isDone }
        }
        if (query.isBlank()) byStatus
        else {
            val q = query.trim().lowercase()
            byStatus.filter { it.title.lowercase().contains(q) }
        }
    }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Cari tugas...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) { Text("Tambah") }

        // Filter controls
        Row(Modifier.padding(vertical = 8.dp)) {
            FilterChip(
                selected = filter == TodoFilter.All,
                onClick = { vm.setFilter(TodoFilter.All) },
                label = { Text("Semua ($totalCount)") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = filter == TodoFilter.Active,
                onClick = { vm.setFilter(TodoFilter.Active) },
                label = { Text("Aktif ($activeCount)") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = filter == TodoFilter.Completed,
                onClick = { vm.setFilter(TodoFilter.Completed) },
                label = { Text("Selesai ($completedCount)") }
            )
        }

        // use HorizontalDivider (material3) to avoid deprecation
        HorizontalDivider()
        LazyColumn {
            items(filteredTodos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.copy(isDone = !todo.isDone)) }, //copy di sini aja lah malash
                    onDelete = { vm.deleteTask(todo) }
                )
            }
        }
    }
}