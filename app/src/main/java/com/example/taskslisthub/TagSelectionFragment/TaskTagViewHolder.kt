package com.example.taskslisthub.TagSelectionFragment

import androidx.recyclerview.widget.RecyclerView
import com.example.taskslisthub.databinding.TaskTagMenuCellBinding
import com.example.taskslisthub.models.TaskTag

class TaskTagViewHolder(
    private val binding: TaskTagMenuCellBinding,
    private val clickListener: ITaskTagClickListener
): RecyclerView.ViewHolder(binding.root){
    fun bindTaskTag(taskTag: TaskTag, isAssignedToTask: Boolean?){
        //display task name
        binding.tagName.text = taskTag.name

        //check box if task is already assigned
        if (isAssignedToTask == null) binding.tagCellContainer.removeView(binding.tagCheckbox)
        else binding.tagCheckbox.isChecked = isAssignedToTask!!
            
        binding.tagCheckbox.setOnClickListener{
            if (binding.tagCheckbox.isChecked) clickListener.selectTaskTag(taskTag)
            else clickListener.deselectTaskTag(taskTag)
        }

        //redirects all cell clicks to the checkbox
        binding.tagCellContainer.setOnClickListener{
            binding.tagCheckbox.performClick()
        }

        //tag menu binding
        binding.menuButton.setOnClickListener {
            clickListener.openMenu(binding.menuButton, taskTag)
        }
    }
}
