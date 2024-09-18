package com.example.googletasksassistant

import android.content.Context
import android.graphics.Paint
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googletasksassistant.databinding.TaskItemCellBinding
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: ITaskItemClickListener
    ): RecyclerView.ViewHolder(binding.root)
{
    //define format for time to be presented in
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    fun bindTaskItem(taskItem: TaskItem){
        //display task name
        binding.name.text = taskItem.name

        //if completed, show task as completed
        if(taskItem.isCompleted()){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }


        binding.completeButton.setImageResource(taskItem.getImageResource())
        binding.completeButton.setColorFilter(taskItem.getImageColor(context))

        binding.completeButton.setOnClickListener{
            clickListener.toggleCompleteTaskItem(taskItem)
        }
        binding.taskCellContainer.setOnClickListener{
            clickListener.editTaskItem(taskItem)
        }
        binding.deleteButton.setOnClickListener{
            clickListener.deleteTaskItem(taskItem)
        }

        binding.addTagButton.setOnClickListener {
            clickListener.openTaskTagMenu(taskItem)
        }

        if(taskItem.formatDueTime() != null){
            binding.dueTime.text = timeFormat.format(taskItem.formatDueTime())
        }
        else
            binding.dueTime.text = ""

        displayTags(taskItem.tags.values.toList())
    }

    private fun displayTags(tags: List<TaskTag>) {
        // Clear previous tags if any
        binding.tagLayout.removeAllViews()

        for (tag in tags) {
            val textView = createTagTextView(tag)
            binding.tagLayout.addView(textView)
        }
    }

    private fun createTagTextView(tag: TaskTag): TextView {
        val textView = TextView(context)
        textView.text = tag.name
        textView.setBackgroundResource(R.drawable.tag_background) // Optional styling
        textView.setPadding(8, 4, 8, 4)  // Optional padding
        return textView
    }
}