package com.example.googletasksassistant

import TaskDatabaseManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class TaskItemRepository(private val db: TaskDatabaseManager)
{
    val allTaskItems: LiveData<List<TaskItem>> = db.tasksLiveData

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem)
    {
        db.insertTask(taskItem)
    }

    @WorkerThread
    suspend fun updateTaskItem(taskItem: TaskItem)
    {
        db.updateTask(taskItem)
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem)
    {
        db.deleteTask(taskItem)
    }
}