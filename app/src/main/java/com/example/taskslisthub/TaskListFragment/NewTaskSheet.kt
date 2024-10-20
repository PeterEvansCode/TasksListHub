package com.example.taskslisthub.TaskListFragment

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.example.taskslisthub.R
import com.example.taskslisthub.TasksListHub
import com.example.taskslisthub.Utilities
import com.example.taskslisthub.databinding.FragmentNewTaskSheetBinding
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime

class NewTaskSheet(
    private val taskItem: TaskItem? = null,
    private val taskTag: TaskTag? = null,
    private var priority: Int = 0
) : BottomSheetDialogFragment() {

    companion object{
        val priorityMap = listOf(
            "No Priority",
            "Low Priority",
            "Medium Priority",
            "High Priority"
        )
    }

    //prevent injections
    private val _illegalCharacters = arrayOf("DELETE", "INSERT", "UPDATE", "PUT", "*", "\"")

    //link using MVVM
    private var _binding: FragmentNewTaskSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel

    //task dueTime
    private var dueTime: LocalTime? = null
    private var dueDate: LocalDate? = null

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
        val repository = (requireActivity().application as TasksListHub).repository
        val factory = TaskItemModelFactory(repository)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        //if editing a taskItem
        if (taskItem != null) {
            //display data relevant to the task
            binding.taskTitle.text = "Edit Task"
            binding.name.text = Editable.Factory.getInstance().newEditable(taskItem.name)
            binding.desc.text = Editable.Factory.getInstance().newEditable(taskItem.desc)
            priority = taskItem.priority
            updateSetPriorityButtonText()

            //update due button
            dueDate = taskItem.formatDueDate()
            dueTime = taskItem.formatDueTime()
            updateDueButtonText()
        }

        //if creating a new task item
        else {
            binding.taskTitle.text = "New Task"
        }

        //button bindings
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.setPriorityButton.setOnClickListener {
            showPriorityPopup(view, taskItem)
        }
        binding.duePickerButton.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        var dateSelected = false

        var year: Int
        var month: Int
        var day: Int

        //set initial date as today if no dueDate given
        if (dueDate == null) {
            // Get the current date to initialize the DatePicker
            val calendar = Calendar.getInstance()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

        //otherwise, set default as due date
        else{
            year = dueDate!!.year
            month = dueDate!!.monthValue - 1
            day = dueDate!!.dayOfMonth
        }

        // Show the DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            //save result
            { _, selectedYear, selectedMonth, selectedDay ->
                dateSelected = true
                dueDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            },
            year, month, day
        )

        // Set OnDismissListener to handle when the dialog is dismissed
        datePickerDialog.setOnDismissListener {
            //only open time picker if date has been selected
            if (dateSelected) {
                openTimePicker()
                dateSelected = false
            }
        }

        datePickerDialog.setTitle("Task Due Date")
        datePickerDialog.show()
    }

    private fun openTimePicker() {
        // Inflate the custom layout for TimePicker from the XML file
        val dialogView = layoutInflater.inflate(R.layout.custom_time_picker, null)
        val timePicker: TimePicker = dialogView.findViewById(R.id.custom_time_picker)
        val leaveBlankButton: Button = dialogView.findViewById(R.id.button_leave_blank)
        val okButton: Button = dialogView.findViewById(R.id.button_ok)

        //set default time
        timePicker.setIs24HourView(true)
        timePicker.hour = dueTime?.hour ?: LocalTime.now().hour
        timePicker.minute = dueTime?.minute ?: LocalTime.now().minute

        // Set up the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Handle "Leave Time Blank" button click
        leaveBlankButton.setOnClickListener {
            dueTime = null  // No time selected, leave blank
            dialog.dismiss()  // Close the dialog
        }

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        // Set a listener on the TimePicker to automatically save and close when time is changed
        timePicker.setOnTimeChangedListener { _, selectedHour, selectedMinute ->
            // Format the selected time
            dueTime = LocalTime.of(selectedHour, selectedMinute)
        }

        dialog.setOnDismissListener{
            updateDueButtonText()
        }

        // Show the dialog
        dialog.setTitle("Task Due Time")
        dialog.show()
    }

    private fun showPriorityPopup(view: View, taskItem: TaskItem?) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(requireContext(), binding.setPriorityButton)
        // Inflate the menu resource
        popupMenu.menuInflater.inflate(R.menu.priority_popup, popupMenu.menu)
        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener { item ->
            var returnVal = false
            when (item.itemId) {
                R.id.priority_high -> {
                    // Handle edit action
                    priority = 3
                    returnVal = true
                }

                R.id.priority_medium -> {
                    // Handle edit action
                    priority = 2
                    returnVal = true
                }

                R.id.priority_low -> {
                    // Handle edit action
                    priority = 1
                    returnVal = true
                }

                R.id.priority_none -> {
                    // Handle edit action
                    priority = 0
                    returnVal = true
                }
                else -> returnVal = false
            }
            updateSetPriorityButtonText()
            returnVal
        }
        // Show the menu
        popupMenu.show()
    }

    private fun updateSetPriorityButtonText(){
        binding.setPriorityButton.text = priorityMap[priority]
    }

    private fun updateDueButtonText() {
        binding.duePickerButton.text = String.format(
            "Due %s",
            Utilities.formatDateTime(dueTime, dueDate)
        )
    }

    private fun saveAction() {
        //save data from sheet
        val name = binding.name.text.toString()

        if(name == "") {
            Toast.makeText(requireContext(), "Name cannot be blank", Toast.LENGTH_SHORT)
                .show()

        }

        else if (
                //check if name contains illegal strings
                _illegalCharacters.firstOrNull {
                    name.contains(it)
                }?.let { illegalCharacter ->
                    //if so, inform user of invalid string
                    Toast.makeText(requireContext(), "Name cannot contain \"$illegalCharacter\"", Toast.LENGTH_SHORT)
                        .show()

                    //return a bool value to satisfy the if statement
                    true } == true
            )

        else{
            val desc = binding.desc.text.toString()
            val dueTimeString = dueTime?.let { TaskItem.timeFormatter.format(it) }
            val dueDateString = dueDate?.let { TaskItem.dateFormatter.format(it) }

            //create new task item if one doesn't already exist
            if (taskItem == null) {
                val newTask = TaskItem(
                    name = name,
                    desc = desc,
                    dueTimeString = dueTimeString,
                    dueDateString = dueDateString,
                    priority = priority
                )
                taskViewModel.addTaskItem(newTask)

                //add tag if currently in a tag folder
                if (taskTag != null) taskViewModel.addTagsToTask(newTask, taskTag)
            }

            //save data in taskItem
            else {
                taskItem.name = name
                taskItem.desc = desc
                taskItem.dueTimeString = dueTimeString
                taskItem.dueDateString = dueDateString
                taskItem.priority = priority
                taskViewModel.updateTaskItem(taskItem)
            }

            //reset sheet
            binding.name.setText("")
            binding.desc.setText("")
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //remove binding
        _binding = null
    }
}
