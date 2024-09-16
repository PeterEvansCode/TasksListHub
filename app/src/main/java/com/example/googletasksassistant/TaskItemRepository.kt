package com.example.googletasksassistant

import com.example.googletasksassistant.models.TaskDatabaseManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import com.example.googletasksassistant.models.taskStores.RecordStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskItemRepository(private val db: TaskDatabaseManager)
{

    //makes all taskTags visible to the UI
    private val _taskTagStore = RecordStore<TaskTag>()
    private val _tagsLiveData = MutableLiveData<List<TaskTag>>()
    val tagsLiveData: LiveData<List<TaskTag>> get() = _tagsLiveData

    //makes all taskItems visible to the UI
    private val _taskItemStore = RecordStore<TaskItem>()
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> get() = _tasksLiveData

    // Coroutine scope for background tasks
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init{
        coroutineScope.launch(Dispatchers.IO){
            loadTasks()
        }
        coroutineScope.launch(Dispatchers.IO){
            loadTags()
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
    }

    @WorkerThread
    suspend fun editTaskTag(taskTag: TaskTag)
    {
        db.updateTag(taskTag)
    }

    @WorkerThread
    suspend fun addTagsToTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //add to task object
        for(taskTag in taskTags) taskItem.tags.add(taskTag)

        //update database
        db.insertTaskTagRelations(taskItem, taskTags)

        return taskItem
    }

    @WorkerThread
    suspend fun removeTagsFromTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //remove from task object
        for(taskTag in taskTags) taskItem.tags.remove(taskTag)

        //update database
        db.deleteTaskTagRelations(taskItem, taskTags)

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

    @WorkerThread
    private suspend fun loadTasks(){
        val currentList = db.getAllTasks()
        _taskItemStore.add(currentList)
        _tasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    @WorkerThread
    private suspend fun loadTags(){
        val currentList = db.getAllTags()
        _taskTagStore.add(currentList)
        _tagsLiveData.postValue(_taskTagStore.getFiltered())
    }

    fun applyTaskSearchFilter(criteria: String){
        _taskItemStore.applySearchFilter(criteria)
        _tasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    fun applyTagSearchFilter(criteria: String){
        _taskTagStore.applySearchFilter(criteria)
        _tagsLiveData.postValue(_taskTagStore.getFiltered())
    }

    //store functions
    private fun addTaskToLocalList(taskItem: TaskItem) {
        _taskItemStore.add(taskItem)
        _tasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    private fun addTagToLocalList(taskTag: TaskTag) {
        _taskTagStore.add(taskTag)
        _tagsLiveData.postValue(_taskTagStore.getFiltered())
    }

    private fun removeTaskFromLocalList(taskItem: TaskItem) {
        _taskItemStore.remove(taskItem)
        _tasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    private fun removeTagFromLocalList(taskTag: TaskTag) {
        _taskTagStore.remove(taskTag)
        _tagsLiveData.postValue(_taskTagStore.getFiltered())
    }
}