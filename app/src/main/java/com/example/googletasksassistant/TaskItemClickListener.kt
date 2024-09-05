package com.example.googletasksassistant

interface TaskItemClickListener
{
    fun editTaskItem(taskItem: TaskItem)
    fun toggleCompleteTaskItem(taskItem: TaskItem)
    fun deleteTaskItem(taskItem: TaskItem)
}