package com.example.taskslisthub.TaskListFragment

import com.example.taskslisthub.models.TaskItem

interface ITaskItemClickListener
{
    fun editTaskItem(taskItem: TaskItem)
    fun toggleCompleteTaskItem(taskItem: TaskItem)
    fun deleteTaskItem(taskItem: TaskItem)
    fun openTaskTagMenu(taskItem: TaskItem)
}