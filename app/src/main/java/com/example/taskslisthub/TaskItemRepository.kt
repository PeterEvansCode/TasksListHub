package com.example.taskslisthub

import android.content.Context
import com.example.taskslisthub.models.TaskDatabaseManager
import androidx.annotation.WorkerThread
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskslisthub.models.GoogleTasksManager
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import com.example.taskslisthub.models.taskStores.RecordStore
import com.example.taskslisthub.models.taskStores.TaskItemStore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskItemRepository(private val db: TaskDatabaseManager)
{
    //API handler for google tasks
    private val _googleTasksManager = GoogleTasksManager()

    //TAGS--------------------------------
    //makes all taskTags visible to the UI
    private val _taskTagStore = RecordStore<TaskTag>()

    //display search results
    private val _filteredTagsLiveData = MutableLiveData<List<TaskTag>>()
    val filteredTagsLiveData: LiveData<List<TaskTag>> get() = _filteredTagsLiveData

    //display all tags
    private val _allTagsLiveData = MutableLiveData<List<TaskTag>>()
    val allTagsLiveData: LiveData<List<TaskTag>> get() = _filteredTagsLiveData

    //for easy access of taskItems based on their tags
    private val _hashOnTags = HashMap<TaskTag, MutableSet<TaskItem>>()

    //TASKS--------------------------------
    //makes all taskItems visible to the UI
    private val _taskItemStore = TaskItemStore()

    //display task search results
    private val _filteredTasksLiveData = MutableLiveData<List<TaskItem>>()
    val filteredTasksLiveData: LiveData<List<TaskItem>> get() = _filteredTasksLiveData

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
    private suspend fun loadTasks(){
        val currentList = db.getAllTasks()
        _taskItemStore.add(currentList)
        _filteredTasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    @WorkerThread
    private suspend fun loadTags(){
        val currentList = db.getAllTags()
        _taskTagStore.add(currentList)
        _filteredTagsLiveData.postValue(_taskTagStore.getFiltered())
    }
    
    fun getTag(tagID: Int): TaskTag?{
        return _taskTagStore.get(tagID)
    }

    fun getTask(taskID: Int): TaskItem?{
        return _taskItemStore.get(taskID)
    }

    @WorkerThread
    suspend fun addTaskItem(taskItem: TaskItem)
    {
        //google tasks
        val taskItemWithGoogleId = _googleTasksManager.newTask(taskItem) //must be run before other methods as it updates the googleId

        //database
        val taskItemWithId = db.insertTask(taskItemWithGoogleId)

        //task store
        _taskItemStore.add(taskItemWithId)
        postTaskValues()
    }

    @WorkerThread
    suspend fun addTaskTag(taskTag: TaskTag)
    {
        //database
        val taskTagWithId = db.insertTag(taskTag)

        //hashmap
        _hashOnTags[taskTag] = mutableSetOf()

        //tag store
        _taskTagStore.add(taskTagWithId)
        postTagValues()
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem)
    {
        //google tasks
        _googleTasksManager.deleteTask(taskItem)

        //hashmap
        for (taskTag in taskItem.tags.values) _hashOnTags[taskTag]?.remove(taskItem)

        //database
        db.deleteTask(taskItem)

        //task store
        _taskItemStore.remove(taskItem)
        postTaskValues()
    }

    @WorkerThread
    suspend fun deleteTaskTag(taskTag: TaskTag)
    {
        //database
        db.deleteTag(taskTag)

        //hashmap and related tasks
        if (_hashOnTags[taskTag] != null)
            for(taskItem in _hashOnTags[taskTag]!!.toList())
                taskItem.tags.remove(taskTag)
        _hashOnTags.remove(taskTag)

        //tag store
        _taskTagStore.remove(taskTag)

        postTagValues()
        postTaskValues()
    }

    @WorkerThread
    suspend fun editTaskItem(taskItem: TaskItem)
    {
        _googleTasksManager.editTask(taskItem)
        db.updateTask(taskItem)
        postTaskValues()
    }

    @WorkerThread
    suspend fun editTaskTag(taskTag: TaskTag)
    {
        db.updateTag(taskTag)
        postTagValues()
    }

    @WorkerThread
    fun addTagsToTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //add to task object
        for(taskTag in taskTags) taskItem.tags.add(taskTag)

        //add to hashmap
        for(taskTag in taskTags) _hashOnTags[taskTag]!!.add(taskItem)

        //update database
        db.insertTaskTagRelations(taskItem, taskTags)

        //notify observers of change
        postTaskValues()

        return taskItem
    }

    @WorkerThread
    fun removeTagsFromTask(taskItem: TaskItem, taskTags: List<TaskTag>): TaskItem
    {
        //remove from task object
        for(taskTag in taskTags) taskItem.tags.remove(taskTag)

        //add to hashmap
        for(taskTag in taskTags) _hashOnTags[taskTag]!!.remove(taskItem)

        //update database
        db.deleteTaskTagRelations(taskItem, taskTags)

        //notify observers of change
        postTaskValues()

        return taskItem
    }

    fun applyTaskSearchFilter(criteria: String){
        _taskItemStore.applySearchFilter(criteria)
        _filteredTasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    fun applyTagSearchFilter(criteria: String){
        _taskTagStore.applySearchFilter(criteria)
        _filteredTagsLiveData.postValue(_taskTagStore.getFiltered())
    }

    fun applyTaskSort(sortBy: Int){
        _taskItemStore.applySort(sortBy)
        postTaskValues()
    }

    private fun postTaskValues(){
        _filteredTasksLiveData.postValue(_taskItemStore.getFiltered())
    }

    private fun postTagValues(){
        _allTagsLiveData.postValue(_taskTagStore.getAll())
        _filteredTagsLiveData.postValue(_taskTagStore.getFiltered())
    }

    //google account
    fun setGoogleAccount(context: Context) {
        _googleTasksManager.setGoogleAccount(context)
    }

    fun getGoogleAccount(): GoogleSignInAccount? {
        return _googleTasksManager.getGoogleAccount()
    }
}