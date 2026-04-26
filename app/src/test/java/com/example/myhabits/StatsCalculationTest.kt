package com.example.myhabits

import com.example.myhabits.data.Habit
import com.example.myhabits.ui.dashboard.StatsState
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StatsCalculationTest {

    @Test
    fun testStreakCalculation() {
        val today = LocalDate.now()
        val habits = listOf(
            Habit(1, "Test", "Goal", "Cat", Color.Red, completions = listOf(today, today.minusDays(1), today.minusDays(2)))
        )
        
        val streak = calculateCurrentStreak(habits.flatMap { it.completions }, today)
        assertEquals(3, streak)
    }

    @Test
    fun testStreakCalculationWithGap() {
        val today = LocalDate.now()
        val habits = listOf(
            Habit(1, "Test", "Goal", "Cat", Color.Red, completions = listOf(today, today.minusDays(2)))
        )
        
        val streak = calculateCurrentStreak(habits.flatMap { it.completions }, today)
        assertEquals(1, streak)
    }
    
    @Test
    fun testStreakCalculationYesterdayOnly() {
        val today = LocalDate.now()
        val habits = listOf(
            Habit(1, "Test", "Goal", "Cat", Color.Red, completions = listOf(today.minusDays(1)))
        )
        
        val streak = calculateCurrentStreak(habits.flatMap { it.completions }, today)
        assertEquals(1, streak)
    }

    private fun calculateCurrentStreak(allCompletions: List<LocalDate>, today: LocalDate): Int {
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
        return currentStreak
    }
}
