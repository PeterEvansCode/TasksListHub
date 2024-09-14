package com.example.googletasksassistant.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.googletasksassistant.TodoApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskDatabaseManager(private val context: Context) {
    //general constants
    private val DATABASE = "task_database.db"

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

    //database
    private val database: SQLiteDatabase

    // Method to delete the database for debugging purposes
    fun deleteDatabase() {
        context.deleteDatabase(DATABASE)
    }

    init {
        deleteDatabase()
        //create a database if one doesn't already exist
        database = try {
            if (TodoApplication.DEBUG) deleteDatabase()
            context.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null)
        } catch (e: SQLiteException) {
            throw e
        }
        onCreate(database)
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
                FOREIGN KEY ($TASKS_TAGS_TASKS_ID) REFERENCES $TASKS_TABLE ($TASK_ID) ON DELETE CASCADE,
                FOREIGN KEY ($TASKS_TAGS_TAGS_ID) REFERENCES $TAGS_TABLE ($TAG_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    /**
     * Insert task
     * @param taskItem Task item to insert
     * @return Returns the taskItem with the updated ID
     */
    suspend fun insertTask(taskItem: TaskItem): TaskItem {
        return withContext(Dispatchers.IO) {
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
            val cursor = database.rawQuery(
                "SELECT last_insert_rowid() AS $TASK_ID",
                null
            )

            //read from cursor and close
            val id: Int
            cursor.use {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                } else {
                    throw Exception("Failed to retrieve last inserted row ID.")
                }
            }

            taskItem.id = id
            taskItem
        }
    }

    suspend fun insertTag(taskTag: TaskTag): TaskTag{
        return withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                INSERT INTO $TAGS_TABLE ($TAG_NAME)
                VALUES (?)
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskTag.name
                )
            )

            //get ID of taskItem
            //position cursor
            val cursor = database.rawQuery(
                "SELECT last_insert_rowid() AS $TAG_ID",
                null
            )

            //read from cursor and close
            val id: Int
            cursor.use {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID))
                } else {
                    throw Exception("Failed to retrieve last inserted row ID.")
                }
            }

            taskTag.id = id
            taskTag
        }
    }

    suspend fun insertTaskTagRelation(taskItem: TaskItem, taskTag: TaskTag){
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                INSERT INTO $TASKS_TAGS_TABLE ($TASKS_TAGS_TASKS_ID, $TASKS_TAGS_TAGS_ID)
                VALUES (?, ?)
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.id,
                    taskTag.id
                )
            )
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
        }
    }

    suspend fun updateTag(taskTag: TaskTag) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                UPDATE $TAGS_TABLE
                SET $TAG_NAME = ?
                WHERE $TAG_ID = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskTag.name
                )
            )
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
        }
    }

    suspend fun deleteTag(taskTag: TaskTag) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                DELETE FROM $TAGS_TABLE
                WHERE $TAG_ID = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskTag.id
                )
            )
        }
    }

    suspend fun deleteTaskTagRelation(taskItem: TaskItem, taskTag: TaskTag) {
        withContext(Dispatchers.IO) {
            database.execSQL(
                //query
                """
                DELETE FROM $TASKS_TAGS_TABLE
                WHERE $TASKS_TAGS_TASKS_ID = ? AND $TASKS_TAGS_TAGS_ID = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.id,
                    taskTag.id
                )
            )
        }
    }

    suspend fun getAllTasks(): MutableList<TaskItem> = withContext(Dispatchers.IO)
    {
        //get tasks
        val query = "SELECT * FROM $TASKS_TABLE"
        val cursor = database.rawQuery(query, null)
        val tasks = HashMap<Int, TaskItem>()

        cursor.use {
            while (cursor.moveToNext()) {
                val taskID = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(TASK_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC))
                val dueTime = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_TIME))
                val completedDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_COMPLETED_DATE))

                tasks.put(taskID, TaskItem(taskID, name, description, dueTime, completedDate))
            }
        }

        val tags = getTags(tasks.keys)

        //find tags for each task
        for (pair in tags)
            tasks[pair.first]!!.tags.add(pair.second)


        // Update LiveData
        tasks.values.toMutableList()
    }

    private fun getTags (taskIDs: Set<Int>): MutableList<Pair<Int, TaskTag>>{
        val tags = mutableListOf<Pair<Int, TaskTag>>() // Pair of taskId and Tag

        if (taskIDs.isEmpty()) return tags // If there are no task IDs

        // Create a parameterized IN clause based on the number of task IDs
        val placeholders = taskIDs.joinToString(",") { "?" }

        val cursor = database.rawQuery(
            """
            SELECT $TAGS_TABLE.$TAG_ID, $TAGS_TABLE.$TAG_NAME, $TASKS_TAGS_TABLE.$TASKS_TAGS_TASKS_ID
            FROM $TAGS_TABLE
            INNER JOIN $TASKS_TAGS_TABLE ON $TAGS_TABLE.$TAG_ID = $TASKS_TAGS_TABLE.$TASKS_TAGS_TAGS_ID
            WHERE $TASKS_TAGS_TABLE.$TASKS_TAGS_TASKS_ID IN ($placeholders)
            """.trimIndent(),

            arrayOf(
                taskIDs.toString()
            )
        )

        //save as taskTag objects
        cursor.use {
            while (cursor.moveToNext()) {
                val taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TASKS_TAGS_TASKS_ID))
                val tagId = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID))
                val tagName = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME))
                tags.add(Pair(taskId, TaskTag(tagId, tagName))) // Assuming you have a Tag class
            }
        }

        return tags
    }
}
