package com.example.taskslisthub

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskslisthub.TagSelectionFragment.ITaskTagClickListener
import com.example.taskslisthub.TagSelectionFragment.TaskTagViewHolder
import com.example.taskslisthub.databinding.TaskTagCellBinding
import com.example.taskslisthub.models.TaskTag

/**
 * Adapter for task tags
 * @param taskTags a sorted list of all tags, where there first n tags are all associated with the specified taskItem, and the rest are not
 * @param numAssociatedTags specifies the number of tags in the taskTags list which are associated with the specified taskItem
 * @param clickListener the clickListener to redirect any button presses to
 */
class TaskTagAdapter(
    private val taskTags: List<TaskTag>,
    private val numAssociatedTags: Int,
    private val clickListener: ITaskTagClickListener
) : RecyclerView.Adapter<TaskTagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskTagViewHolder {
        val binding = TaskTagCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskTagViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = taskTags.size

    override fun onBindViewHolder(holder: TaskTagViewHolder, position: Int) {
        val taskTag = taskTags[position]
        val isAssignedToTask = position < numAssociatedTags
        holder.bindTaskTag(taskTags[position], isAssignedToTask)
    }
}
