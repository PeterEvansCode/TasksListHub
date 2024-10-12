package com.example.taskslisthub

import com.example.taskslisthub.models.TaskDatabaseManager
import android.app.Application

class TasksListHub: Application()
{
    companion object{
        const val DEBUG = false;
    }

    private val database by lazy{ TaskDatabaseManager(this) }
    val repository by lazy { TaskItemRepository(database) }

    init {
        val sharedPreferences = requireContext().getSharedPreferences("YourPrefs", Context.MODE_PRIVATE)
        val colorAccent = sharedPreferences.getInt("onPrimary", R.color.colorAccentDefault) // Default to black if not found
    }
}