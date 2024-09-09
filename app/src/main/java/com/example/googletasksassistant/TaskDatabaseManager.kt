import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.googletasksassistant.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDatabaseManager(context: Context) {

    private val DATABASE_NAME = "simple_database.db"
    private val TABLE_TASKS = "tasks"

    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> get() = _tasksLiveData

    private val database: SQLiteDatabase


    // Internal coroutine scope with a SupervisorJob
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        database = try {
            context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null)
        } catch (e: SQLiteException) {
            throw e
        }
        onCreate(database)
        coroutineScope.launch { loadInitialTasks() }
    }

    private fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            //query
            """
            CREATE TABLE IF NOT EXISTS $TABLE_TASKS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                description TEXT,
                due_time TEXT,
                completed_date TEXT
            )
            """.trimIndent()
        )
    }

    suspend fun insertTask(taskItem: TaskItem) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                INSERT INTO $TABLE_TASKS (name, description, due_time, completed_date)
                VALUES (?, ?, ?, ?)
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.completedDateString
                )
            )

            //get ID of taskItem
            //position cursor
            val query = "SELECT last_insert_rowid() AS id"
            val cursor = database.rawQuery(query, null)

            //read from cursor and close
            val id: Int
            cursor.use {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                } else {
                    throw Exception("Failed to retrieve last inserted row ID.")
                }
            }
            // Add task to local list
            addTaskToLocalList(
                TaskItem(
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.completedDateString,
                    id))
        }
    }

    suspend fun updateTask(taskItem: TaskItem) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                UPDATE $TABLE_TASKS
                SET name = ?, description = ?, due_time = ?, completed_date = ?
                WHERE id = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.completedDateString
                )
            )

            // Update task in local list
            updateTaskInLocalList(taskItem)
        }
    }

    suspend fun deleteTask(taskItem: TaskItem) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                DELETE FROM $TABLE_TASKS
                WHERE id = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.id
                )
            )

            // Remove task from local list
            removeTaskFromLocalList(taskItem.id)
        }
    }

    private suspend fun loadInitialTasks() {
        withContext(Dispatchers.IO) {
            val query = "SELECT * FROM $TABLE_TASKS"
            val cursor = database.rawQuery(query, null)
            val tasks = mutableListOf<TaskItem>()

            cursor.use {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    val dueTime = cursor.getString(cursor.getColumnIndexOrThrow("due_time"))
                    val completedDate = cursor.getString(cursor.getColumnIndexOrThrow("completed_date"))

                    tasks.add(TaskItem(name, description, dueTime, completedDate, id))
                }
            }

            _tasksLiveData.postValue(tasks) // Update LiveData
        }
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

    private fun removeTaskFromLocalList(taskId: Int) {
        val currentList = _tasksLiveData.value?.filter { it.id != taskId } ?: emptyList()
        _tasksLiveData.postValue(currentList)
    }
}
