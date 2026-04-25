package com.example.myhabits.ui.dashboard

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val habitId = inputData.getInt("habitId", -1)
        val habitName = inputData.getString("habitName") ?: "Hábito"

        if (habitId != -1) {
            NotificationHelper.showNotification(context, habitId, habitName)
        }
        return Result.success()
    }
}
