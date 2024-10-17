package com.example.taskslisthub.TaskListFragment

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskslisthub.R
import com.example.taskslisthub.Utilities
import com.example.taskslisthub.databinding.TaskItemCellBinding
import com.example.taskslisthub.databinding.TaskTagCellBinding
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: ITaskItemClickListener
    ): RecyclerView.ViewHolder(binding.root)
{
    fun bindTaskItem(taskItem: TaskItem){
        //display task name
        binding.name.text = taskItem.name

        //if completed, show task as completed
        if(taskItem.isCompleted()){
            binding.completeButton.setImageResource(R.drawable.checked_24)
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

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
            binding.dueText.text = Utilities.formatDateTime(taskItem.formatDueTime(), taskItem.formatDueDate())
        }
        else
            binding.dueText.text = ""

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

    private fun createTagTextView(tag: TaskTag): LinearLayout {
        // Create the binding for tag_item.xml
        val binding = TaskTagCellBinding.inflate(LayoutInflater.from(context))

        // Set the tag name using the binding
        binding.tagTextView.text = tag.name

        // Return the root view (the inflated layout)
        return binding.root
    }
}