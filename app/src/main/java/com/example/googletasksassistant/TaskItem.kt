package com.example.googletasksassistant

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Entity(tableName = "task_item_table")
class TaskItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "dueTime") var dueTime: LocalTime?,
    @ColumnInfo(name = "completedDate") var completedDate: LocalDate?,
    @ColumnInfo(name = "id") var id: UUID = UUID.randomUUID()
)
{
    fun isCompleted() = completedDate != null
    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imageColor(context: Context): Int = if(isCompleted()) purple(context) else black(context)

    private fun purple(context: Context) = ContextCompat.getColor(context, R.color.purple_500)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)
}