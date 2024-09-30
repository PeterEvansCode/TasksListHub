package com.example.taskslisthub

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.taskslisthub.TagSelectionFragment.TagSelectionViewModel
import com.example.taskslisthub.databinding.FragmentNewTagSheetBinding
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalTime

class NewTagSheet(var taskTag: TaskTag?) : BottomSheetDialogFragment() {

    //link using MVVM
    private var _binding: FragmentNewTagSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var tagSelectionViewModel: TagSelectionViewModel

    //task dueTime
    private var dueTime: LocalTime? = null

    //runs just before view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //generate view
        _binding = FragmentNewTagSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    //runs immediately after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get MVVM components
        val repository = (requireActivity().application as TasksListHub).repository
        val factory = TagSelectionViewModel.TagSelectionViewModelFactory(repository)
        tagSelectionViewModel = ViewModelProvider(this, factory)[TagSelectionViewModel::class.java]

        //if editing a taskItem
        if (taskTag != null) {
            //display data relevant to the task
            binding.taskTitle.text = "Edit Tag"
            binding.name.text = Editable.Factory.getInstance().newEditable(taskTag!!.name)
            binding.desc.text = Editable.Factory.getInstance().newEditable(taskTag!!.desc)
        }

        //if creating a new task item
        else {
            binding.taskTitle.text = "New Tag"
        }

        //button bindings
        binding.saveButton.setOnClickListener {
            saveAction()
        }

        // save button is disabled while no title has been entered
        updateSaveButtonState()
        binding.name.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }

            //necessary overrides (no functionality)
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun saveAction() {
        //save data from sheet
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val dueTimeString = dueTime?.let { TaskItem.timeFormatter.format(it) }

        //create new task item if one doesn't already exist
        if (taskTag == null) {
            val newTag = TaskTag(name = name, desc = desc)
            tagSelectionViewModel.addTaskTag(newTag)
        }

        //save data in taskItem
        else {
            taskTag!!.name = name
            taskTag!!.desc = desc
            tagSelectionViewModel.editTaskTag(taskTag!!)
        }

        //reset sheet
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

    /**
     * Disable the save button while no task title is present
     */
    private fun updateSaveButtonState() {
        val name = binding.name.text.toString()
        binding.saveButton.isEnabled = name.isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //remove binding
        _binding = null
    }
}
