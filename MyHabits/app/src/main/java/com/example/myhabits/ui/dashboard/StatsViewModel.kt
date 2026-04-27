package com.example.myhabits.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhabits.data.Habit
import kotlinx.coroutines.flow.*
import java.time.LocalDate

data class StatsState(
    val weeklyCompletionRate: Float = 0f,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val weeklyData: List<Float> = List(7) { 0f },
    val topCategory: String = "Ninguna",
    val streakMessage: String = "",
    val motivationMessage: String = ""
)

class StatsViewModel(dashboardViewModel: DashboardViewModel) : ViewModel() {

    val uiState: StateFlow<StatsState> = dashboardViewModel.habits
        .map { habits ->
            calculateStats(habits)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsState())

    private fun calculateStats(habits: List<Habit>): StatsState {
        if (habits.isEmpty()) return StatsState()

        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val endOfWeek = startOfWeek.plusDays(6)

        val allCompletions = habits.flatMap { it.completions }
        val totalCompletions = allCompletions.size

        val weekCompletions = allCompletions.filter { !it.isBefore(startOfWeek) && !it.isAfter(endOfWeek) }
        val activeHabitsCount = habits.count { !it.isPaused }
        val possibleCompletionsInWeek = activeHabitsCount * 7
        val weeklyRate = if (possibleCompletionsInWeek > 0) weekCompletions.size.toFloat() / possibleCompletionsInWeek else 0f

        val weeklyData = (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            val dailyCompletions = allCompletions.count { it == date }
            if (activeHabitsCount > 0) dailyCompletions.toFloat() / activeHabitsCount else 0f
        }

        val completionDates = allCompletions.distinct().sortedDescending()
        var currentStreak = 0
        if (completionDates.isNotEmpty()) {
            val mostRecent = completionDates.first()
            if (mostRecent == today || mostRecent == today.minusDays(1)) {
                currentStreak = 1
                var checkDate = mostRecent
                for (i in 1 until completionDates.size) {
                    if (completionDates[i] == checkDate.minusDays(1)) {
                        currentStreak++
                        checkDate = completionDates[i]
                    } else break
                }
            }
        }

        val bestStreak = habits.maxOfOrNull { habit ->
            calculateHabitBestStreak(habit.completions)
        } ?: 0

        val topCategory = habits.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.completions.size } }
            .maxByOrNull { it.value }?.key ?: "Ninguna"

        val streakMessage = when {
            currentStreak > 0 -> "¡Llevas $currentStreak días seguidos de disciplina! 🔥"
            else -> "¡Comienza tu racha hoy mismo! ⚡"
        }

        val motivationMessage = if (currentStreak >= bestStreak && bestStreak > 0) {
            "¡Estás en tu mejor racha histórica! 🏆"
        } else {
            "¡No rompas tu racha! Mantente enfocado. 💪"
        }

        return StatsState(
            weeklyCompletionRate = weeklyRate,
            currentStreak = currentStreak,
            bestStreak = maxOf(bestStreak, currentStreak),
            totalCompletions = totalCompletions,
            weeklyData = weeklyData,
            topCategory = topCategory,
            streakMessage = streakMessage,
            motivationMessage = motivationMessage
        )
    }

    private fun calculateHabitBestStreak(completions: List<LocalDate>): Int {
        if (completions.isEmpty()) return 0
        val sorted = completions.distinct().sorted()
        var max = 1
        var current = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] == sorted[i-1].plusDays(1)) {
                current++
            } else {
                max = maxOf(max, current)
                current = 1
            }
        }
        return maxOf(max, current)
    }
}
