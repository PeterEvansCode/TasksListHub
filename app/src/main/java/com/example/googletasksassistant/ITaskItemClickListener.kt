package com.example.googletasksassistant

import com.example.googletasksassistant.models.TaskItem

interface ITaskItemClickListener
{
    fun editTaskItem(taskItem: TaskItem)
    fun toggleCompleteTaskItem(taskItem: TaskItem)
    fun deleteTaskItem(taskItem: TaskItem)
    fun openTaskTagMenu(taskItem: TaskItem)
}