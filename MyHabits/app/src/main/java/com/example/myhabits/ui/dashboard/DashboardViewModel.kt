package com.example.myhabits.ui.dashboard

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhabits.data.Habit
import com.example.myhabits.data.HabitRepository
import com.example.myhabits.data.SessionManager
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime

enum class HabitFilter {
    ALL, FAVORITES, PENDING, COMPLETED
}

class DashboardViewModel(
    application: Application,
    private val repository: HabitRepository = HabitRepository()
) : AndroidViewModel(application) {
    
    private val _userName = MutableStateFlow("USUARIO")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentFilter = MutableStateFlow(HabitFilter.ALL)
    val currentFilter: StateFlow<HabitFilter> = _currentFilter.asStateFlow()

    val habits: StateFlow<List<Habit>> = repository.habits

    val habitsForSelectedDate: StateFlow<List<Habit>> = combine(habits, _selectedDate, _currentFilter) { habitsList, date, filter ->
        habitsList.filter { habit ->
            val isActive = habit.isActiveOn(date) || habit.isCompletedOn(date)
            if (!isActive) return@filter false
            
            when (filter) {
                // En la vista "Todos", ahora solo mostramos los NO completados (pendientes)
                HabitFilter.ALL -> !habit.isCompletedOn(date)
                HabitFilter.FAVORITES -> habit.isFavorite
                HabitFilter.PENDING -> !habit.isCompletedOn(date)
                HabitFilter.COMPLETED -> habit.isCompletedOn(date)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        SessionManager.currentUser
            .onEach { user -> _userName.value = user?.name?.uppercase() ?: "USUARIO" }
            .launchIn(viewModelScope)
        
        NotificationHelper.createNotificationChannel(application)
    }

    fun toggleHabit(habitId: Int) {
        repository.toggleHabit(habitId, _selectedDate.value)
    }
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setFilter(filter: HabitFilter) {
        _currentFilter.value = filter
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
