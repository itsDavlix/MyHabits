package com.example.myhabits.data

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime

class HabitRepository {
    private val _habits = MutableStateFlow(getDefaultHabits())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    fun addHabit(habit: Habit) {
        _habits.update { it + habit }
    }

    fun updateHabit(updatedHabit: Habit) {
        _habits.update { current ->
            current.map { if (it.id == updatedHabit.id) updatedHabit else it }
        }
    }

    fun deleteHabit(habitId: Int) {
        _habits.update { it.filter { h -> h.id != habitId } }
    }

    fun toggleHabit(habitId: Int, date: LocalDate = LocalDate.now()) {
        _habits.update { current ->
            current.map { habit ->
                if (habit.id == habitId) {
                    val isCompleted = habit.completions.contains(date)
                    val newCompletions = if (isCompleted) {
                        habit.completions.filter { it != date }
                    } else {
                        habit.completions + date
                    }
                    habit.copy(completions = newCompletions)
                } else habit
            }
        }
    }

    fun toggleFavorite(habitId: Int) {
        _habits.update { current ->
            current.map { if (it.id == habitId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun togglePaused(habitId: Int): Boolean {
        var newState = false
        _habits.update { current ->
            current.map { 
                if (it.id == habitId) {
                    newState = !it.isPaused
                    it.copy(isPaused = newState)
                } else it 
            }
        }
        return newState
    }

    private fun getDefaultHabits() = listOf(
        Habit(1, "Entrenamiento Fuerza", "60 min", "Poder", Color(0xFFCCFF00), "🏋️"),
        Habit(2, "Running Matutino", "5 km", "Cardio", Color(0xFF00E5FF), "🏃"),
        Habit(3, "Consumo Proteína", "160g", "Nutrición", Color(0xFFFF3D00), "🍗"),
        Habit(4, "Sesión Estiramiento", "15 min", "Flex", Color(0xFFD500F9), "🧘"),
        Habit(5, "Hidratación Elite", "3L", "Recuperación", Color(0xFF2979FF), "💧"),
        Habit(6, "Descanso Reparador", "8h", "Descanso", Color(0xFF76FF03), "🌙")
    )
}
