package com.example.myhabits.ui.dashboard

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhabits.data.Habit
import com.example.myhabits.data.HabitRepository
import com.example.myhabits.data.SessionManager
import kotlinx.coroutines.flow.*
import java.time.LocalTime

class DashboardViewModel(
    application: Application,
    private val repository: HabitRepository = HabitRepository()
) : AndroidViewModel(application) {
    
    private val _userName = MutableStateFlow("USUARIO")
    val userName: StateFlow<String> = _userName.asStateFlow()

    val habits: StateFlow<List<Habit>> = repository.habits

    init {
        SessionManager.currentUser
            .onEach { user -> _userName.value = user?.name?.uppercase() ?: "USUARIO" }
            .launchIn(viewModelScope)
        
        NotificationHelper.createNotificationChannel(application)
    }

    fun toggleHabit(habitId: Int) {
        repository.toggleHabit(habitId)
    }
    
    fun toggleFavorite(habitId: Int) {
        repository.toggleFavorite(habitId)
    }
    
    fun togglePaused(habitId: Int) {
        val newPaused = repository.togglePaused(habitId)
        val habit = habits.value.find { it.id == habitId } ?: return
        
        if (newPaused) {
            ReminderManager.cancelReminder(getApplication(), habit.id)
        } else {
            habit.reminderTime?.let { time ->
                ReminderManager.scheduleReminder(getApplication(), habit.id, habit.name, time)
            }
        }
    }

    fun addHabit(name: String, goal: String, category: String, color: Color, icon: String, frequency: String, reminderTime: LocalTime? = null) {
        val nextId = (habits.value.maxOfOrNull { it.id } ?: 0) + 1
        val newHabit = Habit(
            id = nextId,
            name = name,
            goal = goal,
            category = category,
            categoryColor = color,
            icon = icon,
            frequency = frequency,
            reminderTime = reminderTime
        )
        repository.addHabit(newHabit)
        
        reminderTime?.let { time ->
            ReminderManager.scheduleReminder(getApplication(), nextId, name, time)
        }
    }

    fun updateHabit(updatedHabit: Habit) {
        val oldHabit = habits.value.find { it.id == updatedHabit.id } ?: return
        
        if (oldHabit.reminderTime != updatedHabit.reminderTime || oldHabit.isPaused != updatedHabit.isPaused) {
            if (updatedHabit.reminderTime != null && !updatedHabit.isPaused) {
                ReminderManager.scheduleReminder(getApplication(), updatedHabit.id, updatedHabit.name, updatedHabit.reminderTime)
            } else {
                ReminderManager.cancelReminder(getApplication(), updatedHabit.id)
            }
        }
        
        repository.updateHabit(updatedHabit)
    }

    fun deleteHabit(habitId: Int) {
        repository.deleteHabit(habitId)
        ReminderManager.cancelReminder(getApplication(), habitId)
    }
}
