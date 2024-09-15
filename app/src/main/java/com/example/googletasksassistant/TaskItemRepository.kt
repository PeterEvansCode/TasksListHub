package com.example.googletasksassistant

import com.example.googletasksassistant.models.TaskDatabaseManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskItemRepository(private val db: TaskDatabaseManager)
{

    //makes all taskTags visible to the UI
    private val _tagsLiveData = MutableLiveData<List<TaskTag>>()
    val allTaskTags: LiveData<List<TaskTag>> get() = _tagsLiveData

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
    suspend fun addTaskItem(taskItem: TaskItem)
    {
        val taskItemWithId = db.insertTask(taskItem)
        addTaskToLocalList(taskItemWithId)
    }

    @WorkerThread
    suspend fun addTaskTag(taskTag: TaskTag)
    {
        val taskTagWithId = db.insertTag(taskTag)
        addTagToLocalList(taskTagWithId)
    }

    @WorkerThread
    suspend fun editTaskItem(taskItem: TaskItem)
    {
        db.updateTask(taskItem)
        editTaskInLocalList(taskItem)
    }

    @WorkerThread
    suspend fun editTaskTag(taskTag: TaskTag)
    {
        db.updateTag(taskTag)
        editTagInLocalList(taskTag)
    }

    @WorkerThread
    suspend fun addTagsToTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //add to task object
        for(taskTag in taskTags) taskItem.tags.add(taskTag)

        //update database
        db.insertTaskTagRelations(taskItem, taskTags)

        //update live data
        editTaskInLocalList(taskItem)

        return taskItem
    }

    @WorkerThread
    suspend fun removeTagsFromTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //remove from task object
        for(taskTag in taskTags) taskItem.tags.remove(taskTag)

        //update database
        db.deleteTaskTagRelations(taskItem, taskTags)

        //update live data
        editTaskInLocalList(taskItem)

        return taskItem
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem)
    {
        db.deleteTask(taskItem)
        removeTaskFromLocalList(taskItem)
    }

    @WorkerThread
    suspend fun deleteTaskTag(taskTag: TaskTag)
    {
        db.deleteTag(taskTag)
        removeTagFromLocalList(taskTag)
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

    private fun addTagToLocalList(taskTag: TaskTag) {
        val currentList = _tagsLiveData.value?.toMutableList() ?: mutableListOf()
        currentList.add(taskTag)
        _tagsLiveData.postValue(currentList)
    }

    private fun editTaskInLocalList(taskItem: TaskItem) {
        val currentList = _tasksLiveData.value?.toMutableList() ?: mutableListOf()

        val index = currentList.indexOfFirst { it.id == taskItem.id }
        if (index != -1) {
            currentList[index] = taskItem
        } else {
            throw Exception("The taskItem "+taskItem.id+": "+taskItem.name+" does not exist in the LiveData")
        }

        _tasksLiveData.postValue(currentList)
    }

    private fun editTagInLocalList(taskTag: TaskTag) {
        val currentList = _tagsLiveData.value?.toMutableList() ?: mutableListOf()

        val index = currentList.indexOfFirst { it.id == taskTag.id }
        if (index != -1) {
            currentList[index] = taskTag
        } else {
            throw Exception("The taskTag "+taskTag.id+": "+taskTag.name+" does not exist in the LiveData")
        }

        _tagsLiveData.postValue(currentList)
    }

    private fun removeTaskFromLocalList(taskItem: TaskItem) {
        val currentList = _tasksLiveData.value?.filter { it.id != taskItem.id } ?: emptyList()
        _tasksLiveData.postValue(currentList)
    }

    private fun removeTagFromLocalList(taskTag: TaskTag) {
        val currentList = _tagsLiveData.value?.filter { it.id != taskTag.id } ?: emptyList()
        _tagsLiveData.postValue(currentList)
    }
}