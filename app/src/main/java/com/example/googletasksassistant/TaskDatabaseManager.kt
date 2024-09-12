import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.googletasksassistant.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDatabaseManager(context: Context) {

    //general constants
    private val DATABASE = "simple_database.db"

    //tasks table constants
    private val TASKS_TABLE = "tasks"
    private val TASK_ID = "taskID"
    private val TASK_NAME = "name"
    private val TASK_DESC = "description"
    private val TASK_DUE_TIME = "dueTime"
    private val TASK_COMPLETED_DATE = "completedDate"

    //tags table constants
    private val TAGS_TABLE = "tags"
    private val TAG_ID = "tagID"
    private val TAG_NAME = "tagName"

    //task-tags relation table constants
    private val TASKS_TAGS_TABLE = "taskTagsRelations"
    private val TASKS_TAGS_TASKS_ID = "taskID"
    private val TASKS_TAGS_TAGS_ID = "tagID"


    //makes all taskItems visible to the UI
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> get() = _tasksLiveData

    //database
    private val database: SQLiteDatabase

    // Instantiate coroutine handler
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        //create a database if one doesn't already exist
        database = try {
            context.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null)
        } catch (e: SQLiteException) {
            throw e
        }
        onCreate(database)

        //convert all database entries into taskItems
        coroutineScope.launch { loadInitialTasks() }
    }

    private fun onCreate(db: SQLiteDatabase) {
        //tasks table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TASKS_TABLE (
                $TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TASK_NAME TEXT,
                $TASK_DESC TEXT,
                $TASK_DUE_TIME TEXT,
                $TASK_COMPLETED_DATE TEXT
            )
            """.trimIndent()
        )

        //tags table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TAGS_TABLE (
                $TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TAG_NAME TEXT
            )
            """.trimIndent()
        )

        //task-tags relation table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TASKS_TAGS_TABLE (
                $TASKS_TAGS_TASKS_ID INTEGER,
                $TASKS_TAGS_TAGS_ID INTEGER,
                FOREIGN KEY ($TASKS_TAGS_TASKS_ID) REFERENCES $TASKS_TABLE ($TASK_ID),
                FOREIGN KEY ($TASKS_TAGS_TAGS_ID) REFERENCES $TAGS_TABLE ($TAG_ID)
            )
            """.trimIndent()
        )
    }

    suspend fun insertTask(taskItem: TaskItem) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                INSERT INTO $TASKS_TABLE ($TASK_NAME, $TASK_DESC, $TASK_DUE_TIME, $TASK_COMPLETED_DATE)
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
            val query = "SELECT last_insert_rowid() AS $TASK_ID"
            val cursor = database.rawQuery(query, null)

            //read from cursor and close
            val id: Int
            cursor.use {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                } else {
                    throw Exception("Failed to retrieve last inserted row ID.")
                }
            }
            // Add task to local list
            addTaskToLocalList(
                TaskItem(
                    id,
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.completedDateString
                    ))
        }
    }

    suspend fun updateTask(taskItem: TaskItem) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                UPDATE $TASKS_TABLE
                SET $TASK_NAME = ?, $TASK_DESC = ?, $TASK_DUE_TIME = ?, $TASK_COMPLETED_DATE = ?
                WHERE $TASK_ID = ?
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
                DELETE FROM $TASKS_TABLE
                WHERE $TASK_ID = ?
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
            //get tasks
            val query = "SELECT * FROM $TASKS_TABLE"
            val cursor = database.rawQuery(query, null)
            val tasks = mutableListOf<TaskItem>()

            cursor.use {
                while (cursor.moveToNext()) {
                    val taskID = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(TASK_NAME))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC))
                    val dueTime = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_TIME))
                    val completedDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_COMPLETED_DATE))

                    tasks.add(TaskItem(taskID, name, description, dueTime, completedDate))
                }
            }

            for (task in tasks) {
                database.execSQL("""
                    SELECT $TAGS_TABLE.$TAG_ID, $TAGS_TABLE.$TAG_NAME
                    FROM tags
                    INNER JOIN task_tags ON tags.id = task_tags.tag_id
                    WHERE task_tags.task_id = ?
                """.trimIndent()
                )
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
