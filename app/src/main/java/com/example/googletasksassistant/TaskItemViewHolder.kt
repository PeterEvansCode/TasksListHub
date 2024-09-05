package com.example.googletasksassistant

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.googletasksassistant.databinding.TaskItemCellBinding
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemClickListener
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


            binding.completeButton.setImageResource(taskItem.imageResource())
            binding.completeButton.setColorFilter(taskItem.imageColor(context))

            binding.completeButton.setOnClickListener{
                clickListener.toggleCompleteTaskItem(taskItem)
            }
            binding.taskCellContainer.setOnClickListener{
                clickListener.editTaskItem(taskItem)
            }
            binding.deleteButton.setOnClickListener{
                clickListener.deleteTaskItem(taskItem)
            }

            if(taskItem.dueTime() != null){
                binding.dueTime.text = timeFormat.format(taskItem.dueTime())
            }
            else
                binding.dueTime.text = ""
        }
}