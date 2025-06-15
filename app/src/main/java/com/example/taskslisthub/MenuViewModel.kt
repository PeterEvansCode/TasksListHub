package com.example.taskslisthub

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskslisthub.models.TaskTag

class MenuViewModel(private val repository: TaskItemRepository) : ViewModel()
{
    var allTaskTags: LiveData<List<TaskTag>> = repository.allTagsLiveData

    fun getTaskTag(tagID: Int): TaskTag?{
        return repository.getTag(tagID)
    }
}

class MenuModelFactory(private val repository: TaskItemRepository): ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for ViewModel")
    }
}