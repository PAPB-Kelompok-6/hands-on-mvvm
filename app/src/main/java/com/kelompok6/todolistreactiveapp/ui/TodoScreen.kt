package com.kelompok6.todolistreactiveapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok6.todolistreactiveapp.model.TodoDatabase
import com.kelompok6.todolistreactiveapp.model.TodoRepository
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModel
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModel.TodoFilter
import com.kelompok6.todolistreactiveapp.viewmodel.TodoViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen() {
    val context = LocalContext.current
    val vm: TodoViewModel = run {
        val db = remember { TodoDatabase.getDatabase(context) }
        val repo = remember { TodoRepository(db.todoDao()) }
        val factory = remember { TodoViewModelFactory(repo) }
        viewModel(factory = factory)
    }

    val todos by vm.todos.collectAsState()
    val filter by vm.filter.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var newTitle by rememberSaveable { mutableStateOf("") }

    val filtered = remember(todos, filter, query) {
        val byStatus = when (filter) {
            TodoFilter.All -> todos
            TodoFilter.Active -> todos.filter { !it.isDone }
            TodoFilter.Completed -> todos.filter { it.isDone }
        }
        if (query.isBlank()) byStatus
        else byStatus.filter { it.title.contains(query.trim(), ignoreCase = true) }
    }

    val total = todos.size
    val active = todos.count { !it.isDone }
    val done = total - active

    Scaffold(
        topBar = { HeaderSection() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF0D8A7A),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                    // Search Bar
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search task...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB0B0B0),
                            unfocusedBorderColor = Color(0xFFCDCDCD),
                            focusedContainerColor = Color(0xFFF6F6F6),
                            unfocusedContainerColor = Color(0xFFF6F6F6),
                            cursorColor = Color(0xFF555555),
                            focusedLeadingIconColor = Color(0xFF8A8A8A),
                            unfocusedLeadingIconColor = Color(0xFF8A8A8A),
                            focusedTextColor = Color(0xFF1E1E1E),
                            unfocusedTextColor = Color(0xFF1E1E1E),
                            focusedPlaceholderColor = Color(0xFF9CA3AF),
                            unfocusedPlaceholderColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(2.dp))

                    // Filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PillFilter(
                            label = "All ($total)",
                            selected = filter == TodoFilter.All,
                            onClick = { vm.setFilter(TodoFilter.All) }
                        )
                        PillFilter(
                            label = "Active ($active)",
                            selected = filter == TodoFilter.Active,
                            onClick = { vm.setFilter(TodoFilter.Active) }
                        )
                        PillFilter(
                            label = "Completed ($done)",
                            selected = filter == TodoFilter.Completed,
                            onClick = { vm.setFilter(TodoFilter.Completed) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    Spacer(Modifier.height(12.dp))

                    if (filtered.isEmpty()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No tasks yet. Tap + to add one.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.id }) { todo ->
                                TodoItem(
                                    todo = todo,
                                    onToggle = { vm.toggleTask(todo) },
                                    onDelete = { vm.deleteTask(todo) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF224F23)   // hijau
                    ),
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            vm.addTask(newTitle.trim())
                            newTitle = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF224F23)
                    ),
                    onClick = { showAddDialog = false }
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
            },
            title = {
                Text(
                    "New Task",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF224F23)
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    placeholder = { Text("Task title") },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF224F23),
                        unfocusedBorderColor = Color(0xFFFFFFFF),
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF327537),
                        focusedTextColor = Color(0xFF1A1A1A),
                        unfocusedTextColor = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D8A7A), Color(0xFF05645A))
                )
            )
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "My Task",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PillFilter(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(24.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFEDEDED),
            labelColor = Color(0xFF222222),
            selectedContainerColor = Color(0xFF0D8A7A),
            selectedLabelColor = Color.White
        ),
        border = null,
        modifier = modifier
    )
}
