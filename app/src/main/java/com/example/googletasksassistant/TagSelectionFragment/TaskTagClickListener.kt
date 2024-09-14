package com.example.googletasksassistant.TagSelectionFragment

import com.example.googletasksassistant.models.TaskTag

interface TaskTagClickListener {
    fun toggleSelectTaskTag(taskTag: TaskTag)
}