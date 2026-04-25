package com.example.myhabits.ui.dashboard

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderManager {
    private const val WORK_TAG_PREFIX = "habit_reminder_"

    fun scheduleReminder(context: Context, habitId: Int, habitName: String, reminderTime: LocalTime) {
        val now = LocalDateTime.now()
        var scheduledTime = LocalDateTime.of(now.toLocalDate(), reminderTime)
        
        if (scheduledTime.isBefore(now)) {
            scheduledTime = scheduledTime.plusDays(1)
        }

        val initialDelay = Duration.between(now, scheduledTime)

        val inputData = workDataOf(
            "habitId" to habitId,
            "habitName" to habitName
        )

        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay.toMinutes(), TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag(WORK_TAG_PREFIX + habitId)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG_PREFIX + habitId,
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun cancelReminder(context: Context, habitId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG_PREFIX + habitId)
    }
}
