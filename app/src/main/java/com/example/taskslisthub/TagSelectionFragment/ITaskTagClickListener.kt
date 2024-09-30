package com.example.taskslisthub.TagSelectionFragment

import com.example.taskslisthub.models.TaskTag

interface ITaskTagClickListener {
    fun selectTaskTag(taskTag: TaskTag)
    fun deselectTaskTag(taskTag: TaskTag)
}