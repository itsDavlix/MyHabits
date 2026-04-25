package com.example.myhabits.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.myhabits.data.Habit
import com.example.myhabits.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {
    private val _userName = MutableStateFlow("USUARIO")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _habits = MutableStateFlow(
        listOf(
            Habit(1, "Entrenamiento Fuerza", "60 min", "Poder", Color(0xFFCCFF00), false, "🏋️"),
            Habit(2, "Running Matutino", "5 km", "Cardio", Color(0xFF00E5FF), false, "🏃"),
            Habit(3, "Consumo Proteína", "160g", "Nutrición", Color(0xFFFF3D00), false, "🍗"),
            Habit(4, "Sesión Estiramiento", "15 min", "Flex", Color(0xFFD500F9), false, "🧘"),
            Habit(5, "Hidratación Elite", "3L", "Recuperación", Color(0xFF2979FF), false, "💧"),
            Habit(6, "Descanso Reparador", "8h", "Descanso", Color(0xFF76FF03), false, "🌙"),
        )
    )
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        SessionManager.currentUser.onEach { user ->
            _userName.value = user?.name?.uppercase() ?: "USUARIO"
        }.launchIn(viewModelScope)
    }

    fun toggleHabit(habitId: Int) {
        _habits.update { currentHabits ->
            currentHabits.map {
                if (it.id == habitId) it.copy(isCompleted = !it.isCompleted) else it
            }
        }
    }

    fun addHabit(
        name: String,
        goal: String,
        category: String,
        color: Color,
        icon: String,
        frequency: String
    ) {
        val nextId = (_habits.value.maxOfOrNull { it.id } ?: 0) + 1
        val newHabit = Habit(
            id = nextId,
            name = name,
            goal = goal,
            category = category,
            categoryColor = color,
            icon = icon,
            frequency = frequency
        )
        _habits.update { it + newHabit }
    }

    fun updateHabit(updatedHabit: Habit) {
        _habits.update { currentHabits ->
            currentHabits.map {
                if (it.id == updatedHabit.id) updatedHabit else it
            }
        }
    }

    fun deleteHabit(habitId: Int) {
        _habits.update { currentHabits ->
            currentHabits.filter { it.id != habitId }
        }
    }

    fun toggleFavorite(habitId: Int) {
        _habits.update { currentHabits ->
            currentHabits.map {
                if (it.id == habitId) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    fun togglePaused(habitId: Int) {
        _habits.update { currentHabits ->
            currentHabits.map {
                if (it.id == habitId) it.copy(isPaused = !it.isPaused) else it
            }
        }
    }
}
