package com.example.googletasksassistant

import TaskDatabaseManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.googletasksassistant.models.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TaskItemRepository(private val db: TaskDatabaseManager)
{

    //makes all taskItems visible to the UI
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val allTaskItems: LiveData<List<TaskItem>> get() = _tasksLiveData

    // Coroutine scope for background tasks
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    init{
        coroutineScope.launch(Dispatchers.IO){
            loadTasks()
        }
    }

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem)
    {
        db.insertTask(taskItem)
        addTaskToLocalList(taskItem)
    }

    @WorkerThread
    suspend fun updateTaskItem(taskItem: TaskItem)
    {
        db.updateTask(taskItem)
        updateTaskInLocalList(taskItem)
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem)
    {
        db.deleteTask(taskItem)
        removeTaskFromLocalList(taskItem)
    }

    private suspend fun loadTasks(){
        val currentList = db.getAllTasks()
        _tasksLiveData.postValue(currentList)
    }

    private fun addTaskToLocalList(taskItem: TaskItem) {
        val currentList = _tasksLiveData.value?.toMutableList() ?: mutableListOf()
        currentList.add(taskItem)
        _tasksLiveData.postValue(currentList)
    }

    private fun updateTaskInLocalList(taskItem: TaskItem) {
        val currentList = _tasksLiveData.value?.toMutableList() ?: mutableListOf()

        val index = currentList.indexOfFirst { it.id == taskItem.id }
        if (index != -1) {
            currentList[index] = taskItem
        } else {
            throw Exception("The taskItem "+taskItem.id+": "+taskItem.name+" does not exist in the TaskDatabaseManager LiveData")
        }

        _tasksLiveData.postValue(currentList)
    }

    private fun removeTaskFromLocalList(taskItem: TaskItem) {
        val currentList = _tasksLiveData.value?.filter { it.id != taskItem.id } ?: emptyList()
        _tasksLiveData.postValue(currentList)
    }
}