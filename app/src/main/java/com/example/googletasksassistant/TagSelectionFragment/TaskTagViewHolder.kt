package com.example.googletasksassistant.TagSelectionFragment

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.googletasksassistant.TaskItemClickListener
import com.example.googletasksassistant.databinding.TaskItemCellBinding
import com.example.googletasksassistant.databinding.TaskTagCellBinding
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag

class TaskTagViewHolder(
    private val context: Context,
    private val binding: TaskTagCellBinding,
    private val clickListener: TaskTagClickListener
): RecyclerView.ViewHolder(binding.root){
    fun bindTaskTag(taskTag: TaskTag){
        //display task name
        binding.tagName.text = taskTag.name

        binding.tagCheckbox.setOnClickListener{
            clickListener.toggleSelectTaskTag(taskTag)
        }
        binding.tagCellContainer.setOnClickListener{
            clickListener.toggleSelectTaskTag(taskTag)
            binding.tagCheckbox.isChecked = !binding.tagCheckbox.isChecked
        }
        binding.deleteButton.setOnClickListener{
            clickListener.deleteTaskItem(taskItem)
        }

        if(taskItem.formatDueTime() != null){
            binding.dueTime.text = timeFormat.format(taskItem.formatDueTime())
        }
        else
            binding.dueTime.text = ""
    }
}
