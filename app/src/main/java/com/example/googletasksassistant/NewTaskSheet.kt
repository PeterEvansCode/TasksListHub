package com.example.googletasksassistant

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.googletasksassistant.databinding.FragmentNewTaskSheetBinding
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalTime

class NewTaskSheet(
    val taskItem: TaskItem? = null,
    val taskTag: TaskTag? = null
) : BottomSheetDialogFragment() {

    //link using MVVM
    private var _binding: FragmentNewTaskSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel

    //task dueTime
    private var dueTime: LocalTime? = null

    //runs just before view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //generate view
        _binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    //runs immediately after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get MVVM components
        val repository = (requireActivity().application as TodoApplication).repository
        val factory = TaskItemModelFactory(repository)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        //if editing a taskItem
        if (taskItem != null) {
            //display data relevant to the task
            binding.taskTitle.text = "Edit Task"
            binding.name.text = Editable.Factory.getInstance().newEditable(taskItem.name)
            binding.desc.text = Editable.Factory.getInstance().newEditable(taskItem.desc)
            taskItem.formatDueTime()?.let {
                dueTime = it
                updateTimeButtonText()
            }
        }

        //if creating a new task item
        else {
            binding.taskTitle.text = "New Task"
        }

        //button bindings
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
        updateSaveButtonState()

        // save button is disabled while no title has been entered
        binding.name.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }

            //necessary overrides (no functionality)
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun openTimePicker() {
        //if no current due time, set time picker to default value
        if (dueTime == null)
            dueTime = LocalTime.now()

        //when time is selected
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            //set due time
            dueTime = LocalTime.of(selectedHour, selectedMinute)

            //update UI
            updateTimeButtonText()
        }

        val dialog = TimePickerDialog(
            requireContext(), listener,
            dueTime!!.hour, dueTime!!.minute, true
        )

        //display time picker
        dialog.setTitle("Task Due")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }

    private fun saveAction() {
        //save data from sheet
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val dueTimeString = dueTime?.let { TaskItem.timeFormatter.format(it) }

        //create new task item if one doesn't already exist
        if (taskItem == null) {
            val newTask = TaskItem(name = name, desc = desc, dueTimeString = dueTimeString)
            taskViewModel.addTaskItem(newTask)

            //add tag if currently in a tag folder
            if (taskTag != null) taskViewModel.addTagsToTask(newTask, taskTag)
        }

        //save data in taskItem
        else {
            taskItem.name = name
            taskItem.desc = desc
            taskItem.dueTimeString = dueTimeString
            taskViewModel.updateTaskItem(taskItem!!)
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
