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
    val icon: String = "✨",
    val frequency: String = "Diaria",
    val isFavorite: Boolean = false,
    val isPaused: Boolean = false,
    val completions: List<LocalDate> = emptyList(),
    val reminderTime: LocalTime? = null
) {
    fun isCompletedOn(date: LocalDate): Boolean = completions.contains(date)

    val isCompletedToday: Boolean
        get() = isCompletedOn(LocalDate.now())

    fun isActiveOn(date: LocalDate): Boolean {
        if (isPaused) return false
        return when (frequency) {
            "Diaria" -> true
            "Semanal" -> {
                // For "Semanal", we could define a specific day or just show it every day 
                // but track if it was done once in the week. 
                // Given the request for day-specific views, let's assume it shows every day for now
                // or we could refine this if "Semanal" implies a specific day.
                true 
            }
            else -> {
                if (frequency.contains(",")) {
                    val dayName = when (date.dayOfWeek.value) {
                        1 -> "Lun"
                        2 -> "Mar"
                        3 -> "Mie"
                        4 -> "Jue"
                        5 -> "Vie"
                        6 -> "Sab"
                        7 -> "Dom"
                        else -> ""
                    }
                    frequency.contains(dayName)
                } else true
            }
        }
    }
}
