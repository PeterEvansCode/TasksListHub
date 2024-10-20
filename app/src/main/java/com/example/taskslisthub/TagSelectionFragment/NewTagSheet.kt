package com.example.taskslisthub

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    }

    private fun saveAction() {
        //save data from sheet
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()

        if(name == "") {
            Toast.makeText(requireContext(), "Name cannot be blank", Toast.LENGTH_SHORT)
                .show()

        }

        else if (
        //check if name contains illegal strings
            Utilities.validateString(name)?.let { illegalCharacter ->
                //if so, inform user of invalid string
                Toast.makeText(requireContext(), "Name cannot contain \"$illegalCharacter\"", Toast.LENGTH_SHORT)
                    .show()

                //return a bool value to satisfy the if statement
                true } == true
        )

        else if (
        //check if description contains illegal strings
            Utilities.validateString(desc)?.let { illegalCharacter ->
                //if so, inform user of invalid string
                Toast.makeText(requireContext(), "Description cannot contain \"$illegalCharacter\"", Toast.LENGTH_SHORT)
                    .show()

                //return a bool value to satisfy the if statement
                true } == true
        )

        else{
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
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //remove binding
        _binding = null
    }
}
