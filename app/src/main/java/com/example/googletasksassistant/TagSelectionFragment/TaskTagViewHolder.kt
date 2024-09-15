package com.example.googletasksassistant.TagSelectionFragment

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.googletasksassistant.databinding.TaskTagCellBinding
import com.example.googletasksassistant.models.TaskTag

class TaskTagViewHolder(
    private val context: Context,
    private val binding: TaskTagCellBinding,
    private val clickListener: ITaskTagClickListener
): RecyclerView.ViewHolder(binding.root){
    fun bindTaskTag(taskTag: TaskTag){
        //display task name
        binding.tagName.text = taskTag.name

        binding.tagCheckbox.setOnClickListener{
            if (binding.tagCheckbox.isChecked) clickListener.selectTaskTag(taskTag)
            else clickListener.deselectTaskTag(taskTag)
        }

        //redirects all cell clicks to the checkbox
        binding.tagCellContainer.setOnClickListener{
            binding.tagCheckbox.performClick()
        }
    }
}
