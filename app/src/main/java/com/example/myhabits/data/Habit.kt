package com.example.myhabits.data

import androidx.compose.ui.graphics.Color

data class Habit(
    val id: Int,
    val name: String,
    val goal: String,
    val category: String,
    val categoryColor: Color,
    val isCompleted: Boolean = false,
    val icon: String = "✨"
)
