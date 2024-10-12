package com.example.taskslisthub.models

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.taskslisthub.R
import com.example.taskslisthub.Utilities
import com.example.taskslisthub.models.taskStores.HashOnID
import com.google.api.client.util.DateTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class TaskItem(
    override var id: Int = -1,
    var googleId: String? = null,
    override var name: String,
    var desc: String = "",
    var dueTimeString: String? = null,
    var dueDateString: String? = null,
    var completedDateString: String? = null,
    var tags: HashOnID<TaskTag> = HashOnID(),
    var priority: Int = 0
) : IStandardRecord
{
    //converts completedDateString into a LocalDate type
    fun formatCompletedDate(): LocalDate? = if (completedDateString == null) null
        else LocalDate.parse(completedDateString, dateFormatter)

    //converts dueTimeString into a LocalTime type
    fun formatDueTime(): LocalTime? = if (dueTimeString == null) null
        else LocalTime.parse(dueTimeString, timeFormatter)

    fun formatDueDate(): LocalDate? = if (dueDateString == null) null
    else LocalDate.parse(dueDateString, dateFormatter)

    fun dateTimeGoogleFormat(): DateTime?{
        var returnVal: DateTime? = null
        if (dueDateString != null){
            val date = formatDueDate()
            val instant: Instant

            if (dueTimeString != null) {
                val time = formatDueTime()
                val localDateTime = LocalDateTime.of(date, time)
                instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
            }

            else{
                instant = date!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
            }


            val timeZone = TimeZone.getDefault()
            val tzOffset = timeZone.rawOffset / 60000

            returnVal = DateTime(dueTimeString == null, instant.toEpochMilli(), tzOffset)
        }

        return returnVal
    }

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