package com.example.taskslisthub.models

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.taskslisthub.TasksListHub
import com.example.taskslisthub.models.taskStores.HashOnID

class TaskDatabaseManager(private val context: Context) {
    //general constants
    private val DATABASE = "task_database.db"

    //tasks table constants
    private val TASKS_TABLE = "tasks"
    private val TASK_ID = "taskID"
    private val TASK_GOOGLE_ID = "tasksGoogleID"
    private val TASK_NAME = "taskName"
    private val TASK_DESC = "taskDescription"
    private val TASK_DUE_TIME = "taskDueTime"
    private val TASK_DUE_DATE = "taskDueDate"
    private val TASK_COMPLETED_DATE = "taskCompletedDate"
    private val TASK_PRIORITY = "taskPriority"

    //tags table constants
    private val TAGS_TABLE = "tags"
    private val TAG_ID = "tagID"
    private val TAG_NAME = "tagName"
    private val TAG_DESC = "tagDescription"

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
        //create a database if one doesn't already exist
        database = try {
            if (TasksListHub.DEBUG) deleteDatabase()
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
                $TASK_GOOGLE_ID STRING,
                $TASK_NAME TEXT,
                $TASK_DESC TEXT,
                $TASK_DUE_TIME TEXT,
                $TASK_DUE_DATE TEXT,
                $TASK_COMPLETED_DATE TEXT,
                $TASK_PRIORITY TINYINT
            )
            """.trimIndent()
        )

        //tags table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TAGS_TABLE (
                $TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TAG_NAME TEXT,
                $TAG_DESC TEXT
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
    fun insertTask(taskItem: TaskItem): TaskItem {
            database.execSQL(
                //query
                """
                INSERT INTO $TASKS_TABLE ($TASK_GOOGLE_ID, $TASK_NAME, $TASK_DESC, $TASK_DUE_TIME, $TASK_DUE_DATE, $TASK_COMPLETED_DATE, $TASK_PRIORITY)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.googleId,
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.dueDateString,
                    taskItem.completedDateString,
                    taskItem.priority
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
            return taskItem
    }

    fun insertTag(taskTag: TaskTag): TaskTag{
            database.execSQL(
                //query
                """
                INSERT INTO $TAGS_TABLE ($TAG_NAME, $TAG_DESC)
                VALUES (?, ?)
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskTag.name,
                    taskTag.desc
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
            return taskTag
    }

    fun insertTaskTagRelations(taskItem: TaskItem, taskTags: List<TaskTag>) {
            if (taskTags.isNotEmpty()) {
                // Create a comma-separated list of placeholders for SQL
                val placeholders = taskTags.joinToString(separator = ",") { "(?, ?)" }

                val sql = """
                INSERT INTO $TASKS_TAGS_TABLE ($TASKS_TAGS_TASKS_ID, $TASKS_TAGS_TAGS_ID)
                VALUES $placeholders
            """.trimIndent()

                // Prepare the arguments array
                val args = mutableListOf<Int>()
                for (taskTag in taskTags) {
                    args.add(taskItem.id)
                    args.add(taskTag.id)
                }

                // Execute the SQL statement with arguments
                database.execSQL(sql, args.toTypedArray())
            }
    }

    fun updateTask(taskItem: TaskItem) {
            database.execSQL(
                //query
                """
                UPDATE $TASKS_TABLE
                SET $TASK_GOOGLE_ID = ?, $TASK_NAME = ?, $TASK_DESC = ?, $TASK_DUE_TIME = ?, $TASK_DUE_DATE = ?, $TASK_COMPLETED_DATE = ?, $TASK_PRIORITY = ?
                WHERE $TASK_ID = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskItem.googleId,
                    taskItem.name,
                    taskItem.desc,
                    taskItem.dueTimeString,
                    taskItem.dueDateString,
                    taskItem.completedDateString,
                    taskItem.priority,
                    taskItem.id
                )
            )

        val debug = getAllTasks()
        val debug2 = null
    }

    fun updateTag(taskTag: TaskTag) {
            database.execSQL(
                //query
                """
                UPDATE $TAGS_TABLE
                SET $TAG_NAME = ?, $TAG_DESC = ?
                WHERE $TAG_ID = ?
                """.trimIndent(),

                //arguments
                arrayOf(
                    taskTag.name,
                    taskTag.desc,
                    taskTag.id
                )
            )
    }

    fun deleteTask(taskItem: TaskItem) {
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

    fun deleteTag(taskTag: TaskTag) {
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

    fun deleteTaskTagRelations(taskItem: TaskItem, taskTags: List<TaskTag>) {
            if (taskTags.isNotEmpty()) {
                // Create a comma-separated list of placeholders for SQL
                val placeholders = taskTags.joinToString(separator = ",") { "?" }

                val sql = """
                DELETE FROM $TASKS_TAGS_TABLE
                WHERE $TASKS_TAGS_TASKS_ID = ? AND $TASKS_TAGS_TAGS_ID IN ($placeholders)
            """.trimIndent()

                // Prepare the arguments array
                val args = mutableListOf<Any>()
                args.add(taskItem.id)
                for (taskTag in taskTags) {
                    args.add(taskTag.id)
                }

                // Execute the SQL statement with arguments
                database.execSQL(sql, arrayOf(args))
            }
    }

    fun getAllTasks(): MutableList<TaskItem>
    {
        //get tasks
        val query = "SELECT * FROM $TASKS_TABLE"
        val cursor = database.rawQuery(query, null)
        val tasks = HashOnID<TaskItem>()

        cursor.use {
            while (cursor.moveToNext()) {
                val taskID = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_ID))
                val tasksGoogleId = cursor.getString(cursor.getColumnIndexOrThrow(TASK_GOOGLE_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(TASK_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DESC))
                val dueTime = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_TIME))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_DUE_DATE))
                val completedDate = cursor.getString(cursor.getColumnIndexOrThrow(TASK_COMPLETED_DATE))
                val priority = cursor.getInt(cursor.getColumnIndexOrThrow(TASK_PRIORITY))

                tasks.add(TaskItem(id = taskID, googleId = tasksGoogleId, name = name, desc = description, dueTimeString = dueTime, dueDateString = dueDate, completedDateString = completedDate, priority = priority))
            }
        }

        val tags = getTags(tasks.keys)

        //find tags for each task
        for ((taskID, taskTag) in tags)
            tasks[taskID]!!.tags.add(taskTag)

        // Update LiveData
        return tasks.values.toMutableList()
    }

    fun getAllTags(): MutableList<TaskTag>
    {
        //run query
        val query = "SELECT * FROM $TAGS_TABLE"
        val cursor = database.rawQuery(query, null)

        //read all tags
        val tags = mutableListOf<TaskTag>()
        cursor.use {
            while (cursor.moveToNext()) {
                val tagID = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(TAG_DESC))

                tags.add(TaskTag(id = tagID, name = name, desc = description))
            }
        }

        //return tags
        return tags
    }

    private fun getTags (taskIDs: Set<Int>): MutableList<Pair<Int, TaskTag>>{
        val tags = mutableListOf<Pair<Int, TaskTag>>() // Pair of taskId and Tag

        if (taskIDs.isEmpty()) return tags // If there are no task IDs

        // Create a parameterized IN clause based on the number of task IDs
        val placeholders = taskIDs.joinToString(",") { "?" }
        val query = """
            SELECT $TAGS_TABLE.$TAG_ID, $TAGS_TABLE.$TAG_NAME, $TAGS_TABLE.$TAG_DESC, $TASKS_TAGS_TABLE.$TASKS_TAGS_TASKS_ID
            FROM $TAGS_TABLE
            INNER JOIN $TASKS_TAGS_TABLE ON $TAGS_TABLE.$TAG_ID = $TASKS_TAGS_TABLE.$TASKS_TAGS_TAGS_ID
            WHERE $TASKS_TAGS_TABLE.$TASKS_TAGS_TASKS_ID IN ($placeholders)
            """.trimIndent()

        val args = taskIDs.map { it.toString() }.toTypedArray()

        val cursor = database.rawQuery(query, args)

        //save as taskTag objects
        cursor.use {
            while (cursor.moveToNext()) {
                val taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TASKS_TAGS_TASKS_ID))
                val tagId = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID))
                val tagName = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME))
                val tagDesc = cursor.getString(cursor.getColumnIndexOrThrow(TAG_DESC))
                tags.add(Pair(taskId, TaskTag(id = tagId, name = tagName, desc = tagDesc))) // Assuming you have a Tag class
            }
        }

        return tags
    }

    fun reportTaskTagRelations(): Int{
        val cursor = database.rawQuery("SELECT COUNT(*) FROM $TASKS_TAGS_TABLE", null)

        cursor.moveToFirst() // Ensure the cursor is pointing to the result
        val count = cursor.getInt(0) // Get the first column of the result, which is the count
        cursor.close() // Always close the cursor when done
        return count
    }

    private fun getAllTaskTagRelationsAsTags (): MutableList<Pair<Int, TaskTag>>{
        val tags = mutableListOf<Pair<Int, TaskTag>>() // Pair of taskId and Tag

        val query = """
            SELECT *
            FROM $TAGS_TABLE
            INNER JOIN $TASKS_TAGS_TABLE ON $TAGS_TABLE.$TAG_ID = $TASKS_TAGS_TABLE.$TASKS_TAGS_TAGS_ID
            """.trimIndent()


        val cursor = database.rawQuery(query, null)

        //save as taskTag objects
        cursor.use {
            while (cursor.moveToNext()) {
                val taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TASKS_TAGS_TASKS_ID))
                val tagId = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID))
                val tagName = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME))
                val tagDesc = cursor.getString(cursor.getColumnIndexOrThrow(TAG_DESC))
                tags.add(Pair(taskId, TaskTag(id = tagId, name = tagName, desc = tagDesc))) // Assuming you have a Tag class
            }
        }

        return tags
    }

    private fun getAllTaskTagRelations (): MutableList<Pair<Int, Int>>{
        val relations = mutableListOf<Pair<Int, Int>>() // Pair of taskId and Tag

        val query = """
            SELECT *
            FROM $TASKS_TAGS_TABLE
            """.trimIndent()


        val cursor = database.rawQuery(query, null)

        //save as taskTag objects
        cursor.use {
            while (cursor.moveToNext()) {
                relations.add(Pair(cursor.getInt(cursor.getColumnIndexOrThrow(TASKS_TAGS_TASKS_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(TASKS_TAGS_TAGS_ID))))
            }
        }

        return relations
    }
}