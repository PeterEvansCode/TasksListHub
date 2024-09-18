package com.example.googletasksassistant

import com.example.googletasksassistant.models.TaskDatabaseManager
import android.app.Application

class TodoApplication: Application()
{
    companion object{
        const val DEBUG = true;
    }

    private val database by lazy{ TaskDatabaseManager(this) }
    val repository by lazy { TaskItemRepository(database) }
}