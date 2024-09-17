package com.example.googletasksassistant

import android.location.Criteria
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MenuViewModel(private val repository: TaskItemRepository) : ViewModel()
{
    var taskTags: LiveData<List<TaskTag>> = repository.tagsLiveData

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