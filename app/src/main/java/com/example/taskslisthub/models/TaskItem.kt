package com.example.taskslisthub.models

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
    var priority: Int = 0,
) : IStandardRecord
{
    val timeMade = LocalTime.now()
    val dateMade = LocalDate.now()

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

    //date and time formatters
    companion object{
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}