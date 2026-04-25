package com.example.myhabits.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalTime

data class Habit(
    val id: Int,
    val name: String,
    val goal: String,
    val category: String,
    val categoryColor: Color,
    val isCompleted: Boolean = false,
    val icon: String = "✨",
    val frequency: String = "Diaria",
    val isFavorite: Boolean = false,
    val isPaused: Boolean = false,
    val completions: List<LocalDate> = emptyList(),
    val reminderTime: LocalTime? = null
)
