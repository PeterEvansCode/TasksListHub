package com.example.googletasksassistant.TagSelectionFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.googletasksassistant.TaskItemRepository
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagSelectionViewModel(private val repository: TaskItemRepository) : ViewModel() {
    var taskTags: LiveData<List<TaskTag>> = repository.filteredTagsLiveData

    fun addTaskTag(newTag: TaskTag) = viewModelScope.launch(Dispatchers.IO) {
        repository.addTaskTag(newTag)
    }

    fun editTaskTag(taskTag: TaskTag) = viewModelScope.launch(Dispatchers.IO) {
        repository.editTaskTag(taskTag)
    }

    fun deleteTaskTag(taskTag: TaskTag) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTaskTag(taskTag)
    }

    fun addTagsToTask(taskItem: TaskItem, taskTags: List<TaskTag>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTagsToTask(taskItem, taskTags)
        }

    fun searchForTags(criteria: String) = repository.applyTagSearchFilter(criteria)

    fun removeTagsFromTask(taskItem: TaskItem, taskTags: List<TaskTag>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeTagsFromTask(taskItem, taskTags)
        }

    class TagSelectionViewModelFactory(private val repository: TaskItemRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TagSelectionViewModel::class.java)) {
                return TagSelectionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
