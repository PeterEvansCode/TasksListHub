package com.example.googletasksassistant

import TaskDatabaseManager
import android.app.Application

class TodoApplication: Application()
{
    private val database by lazy{ TaskDatabaseManager(this)}
    val repository by lazy { TaskItemRepository(database) }
}