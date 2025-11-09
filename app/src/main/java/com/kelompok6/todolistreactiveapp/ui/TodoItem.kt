package com.kelompok6.todolistreactiveapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kelompok6.todolistreactiveapp.model.Todo
import kotlinx.datetime.*

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val created = remember(todo.createdAt) {
        Instant.fromEpochMilliseconds(todo.createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val createdFormatted = remember(created) {
        "%04d-%02d-%02d %02d:%02d".format(
            created.year, created.monthNumber, created.dayOfMonth, created.hour, created.minute
        )
    }

    val accent = if (todo.isDone) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val shape = RoundedCornerShape(22.dp)

    Surface(
        shape = shape,
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(6.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = if (todo.isDone)
                        TextStyle(textDecoration = TextDecoration.LineThrough)
                    else MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Created: $createdFormatted",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                StatusBadge(
                    text = if (todo.isDone) "COMPLETED" else "ACTIVE",
                    color = accent
                )
            }

            Spacer(Modifier.width(12.dp))

            // Progress Ring (0% / 100%)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() }
            ) {
                val progress = if (todo.isDone) 1f else 0f
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.matchParentSize(),
                    color = accent,
                    strokeWidth = 5.dp,
                    trackColor = accent.copy(alpha = 0.2f)
                )
                Text(
                    if (todo.isDone) "100%" else "0%",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.width(6.dp))

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(50),
        shadowElevation = 0.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
