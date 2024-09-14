package com.example.googletasksassistant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletasksassistant.TagSelectionFragment.TaskTagClickListener
import com.example.googletasksassistant.TagSelectionFragment.TaskTagViewHolder
import com.example.googletasksassistant.databinding.TaskItemCellBinding
import com.example.googletasksassistant.databinding.TaskTagCellBinding
import com.example.googletasksassistant.models.TaskTag

class TaskTagAdapter(
    private val taskTags: List<TaskTag>,
    private val clickListener: TaskTagClickListener
) : RecyclerView.Adapter<TaskTagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskTagViewHolder {
        val binding = TaskTagCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskTagViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = taskItems.size

    override fun onBindViewHolder(holder: TaskTagViewHolder, position: Int) {
        holder.bindTaskTag(taskTags[position])
    }
}
