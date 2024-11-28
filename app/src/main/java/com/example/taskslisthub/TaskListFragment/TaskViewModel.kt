package com.example.taskslisthub.TaskListFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskslisthub.TaskItemRepository
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskItemRepository) : ViewModel()
{
    var taskItems: LiveData<List<TaskItem>> = repository.filteredTasksLiveData

    //selected tasks
    private val _selectedTasks = mutableListOf<TaskItem>()

    fun addTaskItem(newTask: TaskItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.addTaskItem(newTask)
    }

    fun updateTaskItem(taskItem: TaskItem)= viewModelScope.launch(Dispatchers.IO) {
        repository.editTaskItem(taskItem)
    }

    fun setCompleted(taskItem: TaskItem)= viewModelScope.launch(Dispatchers.IO) {
        if(!taskItem.isCompleted())
            taskItem.completedDateString = TaskItem.dateFormatter.format(LocalDate.now())
        repository.editTaskItem(taskItem)
    }

    fun undoCompleted(taskItem: TaskItem)= viewModelScope.launch(Dispatchers.IO) {
        if(taskItem.isCompleted())
            taskItem.completedDateString = null
        repository.editTaskItem(taskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem)= viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTaskItem(taskItem)
    }

    fun searchForTask(criteria: String){
        repository.applyTaskSearchFilter(criteria)
    }

    fun applyTaskSort(sortId: Int){
        repository.applyTaskSort(sortId)
    }

    fun addTagsToTask(taskItem: TaskItem, taskTags: List<TaskTag>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTagsToTask(taskItem, taskTags)
        }

    fun addTagsToTask(taskItem: TaskItem, taskTag: TaskTag) = addTagsToTask(taskItem, listOf(taskTag))

    fun removeTagsFromTask(taskItem: TaskItem, taskTags: List<TaskTag>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeTagsFromTask(taskItem, taskTags)
        }

    fun removeTagsFromTask(taskItem: TaskItem, taskTag: TaskTag) = removeTagsFromTask(taskItem, listOf(taskTag))

    fun setGoogleAccount(context: Context) {
        repository.setGoogleAccount(context)
    }

    fun addTaskToSelection(taskItem: TaskItem) {
        _selectedTasks.add(taskItem)
    }

    fun removeTaskFromSelection(taskItem: TaskItem) {
        _selectedTasks.remove(taskItem)
    }

    fun hasSelection(): Boolean = _selectedTasks.size > 0
}

class TaskItemModelFactory(private val repository: TaskItemRepository): ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown Class for ViewModel")
    }
}