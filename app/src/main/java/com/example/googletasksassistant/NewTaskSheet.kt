package com.example.googletasksassistant

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.googletasksassistant.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalTime

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private var _binding: FragmentNewTaskSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel

    private var dueTime: LocalTime? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as TodoApplication).repository
        val factory = TaskItemModelFactory(repository)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        if (taskItem != null) {
            binding.taskTitle.text = "Edit Task"
            binding.name.text = Editable.Factory.getInstance().newEditable(taskItem!!.name)
            binding.desc.text = Editable.Factory.getInstance().newEditable(taskItem!!.desc)
            taskItem!!.dueTime()?.let {
                dueTime = it
                updateTimeButtonText()
            }
        } else {
            binding.taskTitle.text = "New Task"
        }

        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
    }

    private fun openTimePicker() {
        if (dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(
            requireContext(), listener,
            dueTime!!.hour, dueTime!!.minute, true
        )
        dialog.setTitle("Task Due")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val dueTimeString = dueTime?.let { TaskItem.timeFormatter.format(it) }

        if (taskItem == null) {
            val newTask = TaskItem(name, desc, dueTimeString, null)
            taskViewModel.addTaskItem(newTask)
        } else {
            taskItem!!.name = name
            taskItem!!.desc = desc
            taskItem!!.dueTimeString = dueTimeString
            taskViewModel.updateTaskItem(taskItem!!)
        }

        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
