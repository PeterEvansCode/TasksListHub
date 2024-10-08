package com.example.googletasksassistant.models

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.googletasksassistant.R
import com.example.googletasksassistant.models.taskStores.HashOnID
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TaskItem(
    override var id: Int = 0,
    override var name: String,
    var desc: String = "",
    var dueTimeString: String? = null,
    var completedDateString: String? = null,
    var tags: HashOnID<TaskTag> = HashOnID()
) : IStandardRecord
{
    //converts completedDateString into a LocalDate type
    fun formatCompletedDate(): LocalDate? = if (completedDateString == null) null
        else LocalDate.parse(completedDateString, dateFormatter)

    //converts dueTimeString into a LocalTime type
    fun formatDueTime(): LocalTime? = if (dueTimeString == null) null
        else LocalTime.parse(dueTimeString, timeFormatter)

    //check if task has been completed
    fun isCompleted() = formatCompletedDate() != null

    //UI information
    //PROBABLY SHOULDN'T BE IN THE MODEL
    fun getImageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun getImageColor(context: Context): Int = if(isCompleted()) purple(context) else black(context)
    private fun purple(context: Context) = ContextCompat.getColor(context, R.color.purple_500)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)

    //date and time formatters
    companion object{
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}