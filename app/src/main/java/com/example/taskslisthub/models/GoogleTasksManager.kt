package com.example.taskslisthub.models

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoogleTasksManager() {

    private lateinit var tasksService: Tasks
    private var tasksListId: String? = null

    // Link to Google account
    private var googleAccount: GoogleSignInAccount? = null

    // Set Google account
    fun setGoogleAccount(context: Context) {
        googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        googleAccount?.let { setupTasksService(context) }
    }

    // Get Google account
    fun getGoogleAccount(): GoogleSignInAccount? {
        return googleAccount
    }

    private fun setupTasksService(context: Context) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf("https://www.googleapis.com/auth/tasks")
        )
        credential.selectedAccount = googleAccount?.account

        // Create the Google Tasks API client
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        tasksService = Tasks.Builder(transport, jsonFactory, credential)
            .setApplicationName("TasksListHub")
            .build()

        //create task list for tasksHub if one doesn't already exist
        CoroutineScope(Dispatchers.IO).launch{
            createTaskList("Tasks List Hub")
        }
    }

    suspend fun createTaskList(title: String) {
        withContext(Dispatchers.IO){
        try {
            // Step 1: Retrieve all existing task lists
            val taskLists = tasksService.tasklists().list().execute().items

            // Step 2: Check if "Tasks List Hub" already exists
            val tasksList = taskLists.find { it.title == title }

            if (tasksList == null) {
                // Step 3: Create "Tasks List Hub" if it doesn't exist
                val taskList = TaskList().setTitle(title)
                val createdTaskList = tasksService.tasklists().insert(taskList).execute()
                tasksListId = createdTaskList.id

                Log.d("TAG", "Task list created with ID: ${createdTaskList.id}")
            } else {
                Log.d("TAG", "\"${title}\" already exists.")

                // Retrieve all task lists
                tasksListId = tasksList.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error while creating task list: ${e.message}")
        }
            }
    }

    /**
     * Adds task to google tasks and updates the taskItem's googleId field
     * @param taskItem The task to be inserted
     * @return Returns the passed in taskItem (unnecessary but helps to show that the taskItem has been updated)
     */
    fun newTask(taskItem: TaskItem): TaskItem{
        try {

            val newTask = Task().apply {
                title = taskItem.name
                notes = taskItem.desc
                due = taskItem.dateTimeGoogleFormat()
            }

            val createdTask = tasksService.tasks().insert(tasksListId, newTask).execute()

            //update taskItem with googleId
            taskItem.apply{
                googleId = createdTask.id
            }

            println("Task edited successfully.")
        }
        catch(e: Exception){
            println("Error editing task: ${e.message}")
        }
        return taskItem
    }

    fun editTask(taskItem: TaskItem){

        try{
            val tasks = getAllTasks()

            val editedTask = Task().apply {
                id = taskItem.googleId
                title = taskItem.name
                notes = taskItem.desc
                due = taskItem.dateTimeGoogleFormat()
            }

            tasksService.tasks().update(tasksListId, taskItem.googleId, editedTask).execute()
            println("Task edited successfully.")
        }catch(e: Exception){
            println("Task edit failed.")
        }

    }

    fun deleteTask(taskItem: TaskItem) {
        try {
            tasksService.tasks().delete(tasksListId, taskItem.googleId).execute()
            println("Task deleted successfully.")
        } catch (e: Exception) {
            println("Error deleting task: ${e.message}")
        }
    }

    fun getAllTasks(): List<Task> {
        try {
            // Fetch all tasks for the specific task list ID
            val tasks = tasksService.tasks().list(tasksListId).execute()

            tasks.items?.let {
                println("Tasks retrieved successfully from task list ID: $tasksListId")
                return it
            } ?: run {
                println("No tasks found for the given task list ID.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error retrieving tasks: ${e.message}")
        }

        return mutableListOf()
    }
}
