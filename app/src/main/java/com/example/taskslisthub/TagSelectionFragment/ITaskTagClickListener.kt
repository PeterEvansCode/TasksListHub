package com.example.taskslisthub.TagSelectionFragment

import android.view.View
import com.example.taskslisthub.models.TaskTag

interface ITaskTagClickListener {
    fun selectTaskTag(taskTag: TaskTag)
    fun deselectTaskTag(taskTag: TaskTag)
    fun openMenu(view: View, taskTag: TaskTag)
}