package com.example.googletasksassistant

import com.example.googletasksassistant.models.TaskItem

interface TaskItemClickListener
{
    fun editTaskItem(taskItem: TaskItem)
    fun toggleCompleteTaskItem(taskItem: TaskItem)
    fun deleteTaskItem(taskItem: TaskItem)
}