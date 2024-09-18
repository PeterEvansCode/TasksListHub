package com.example.googletasksassistant.TagSelectionFragment

import com.example.googletasksassistant.models.TaskTag

interface ITaskTagClickListener {
    fun selectTaskTag(taskTag: TaskTag)
    fun deselectTaskTag(taskTag: TaskTag)
}