package com.example.myhabits.ui.dashboard

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhabits.data.Habit
import com.example.myhabits.data.SessionManager
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _userName = MutableStateFlow("USUARIO")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _habits = MutableStateFlow(getDefaultHabits())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        SessionManager.currentUser
            .onEach { user -> _userName.value = user?.name?.uppercase() ?: "USUARIO" }
            .launchIn(viewModelScope)
        
        NotificationHelper.createNotificationChannel(application)
    }

    private fun updateHabitState(habitId: Int, transform: (Habit) -> Habit) {
        _habits.update { current ->
            current.map { if (it.id == habitId) transform(it) else it }
        }
    }

    fun toggleHabit(habitId: Int) {
        val today = LocalDate.now()
        updateHabitState(habitId) { habit ->
            val isCurrentlyCompleted = habit.completions.contains(today)
            val newCompletions = if (isCurrentlyCompleted) {
                habit.completions.filter { it != today }
            } else {
                habit.completions + today
            }
            habit.copy(
                isCompleted = !isCurrentlyCompleted,
                completions = newCompletions
            )
        }
    }
    
    fun toggleFavorite(habitId: Int) = updateHabitState(habitId) { it.copy(isFavorite = !it.isFavorite) }
    
    fun togglePaused(habitId: Int) {
        updateHabitState(habitId) { habit ->
            val newPaused = !habit.isPaused
            if (newPaused) {
                ReminderManager.cancelReminder(getApplication(), habit.id)
            } else {
                habit.reminderTime?.let { time ->
                    ReminderManager.scheduleReminder(getApplication(), habit.id, habit.name, time)
                }
            }
            habit.copy(isPaused = newPaused)
        }
    }

    fun addHabit(name: String, goal: String, category: String, color: Color, icon: String, frequency: String, reminderTime: LocalTime? = null) {
        val nextId = (_habits.value.maxOfOrNull { it.id } ?: 0) + 1
        val newHabit = Habit(nextId, name, goal, category, color, false, icon, frequency, reminderTime = reminderTime)
        _habits.update { it + newHabit }
        
        reminderTime?.let { time ->
            ReminderManager.scheduleReminder(getApplication(), nextId, name, time)
        }
    }

    fun updateHabit(updatedHabit: Habit) {
        _habits.update { current -> 
            current.map { 
                if (it.id == updatedHabit.id) {
                    if (it.reminderTime != updatedHabit.reminderTime) {
                        if (updatedHabit.reminderTime != null && !updatedHabit.isPaused) {
                            ReminderManager.scheduleReminder(getApplication(), updatedHabit.id, updatedHabit.name, updatedHabit.reminderTime)
                        } else {
                            ReminderManager.cancelReminder(getApplication(), updatedHabit.id)
                        }
                    }
                    updatedHabit 
                } else it 
            } 
        }
    }

    fun deleteHabit(habitId: Int) {
        _habits.update { it.filter { h -> h.id != habitId } }
        ReminderManager.cancelReminder(getApplication(), habitId)
    }

    private fun getDefaultHabits() = listOf(
        Habit(1, "Entrenamiento Fuerza", "60 min", "Poder", Color(0xFFCCFF00), false, "🏋️"),
        Habit(2, "Running Matutino", "5 km", "Cardio", Color(0xFF00E5FF), false, "🏃"),
        Habit(3, "Consumo Proteína", "160g", "Nutrición", Color(0xFFFF3D00), false, "🍗"),
        Habit(4, "Sesión Estiramiento", "15 min", "Flex", Color(0xFFD500F9), false, "🧘"),
        Habit(5, "Hidratación Elite", "3L", "Recuperación", Color(0xFF2979FF), false, "💧"),
        Habit(6, "Descanso Reparador", "8h", "Descanso", Color(0xFF76FF03), false, "🌙")
    )
}
