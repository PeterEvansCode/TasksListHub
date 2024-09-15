package com.example.googletasksassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googletasksassistant.TagSelectionFragment.TagSelectionFragment
import com.example.googletasksassistant.databinding.FragmentMainTaskListBinding
import com.example.googletasksassistant.models.TaskItem

class MainTaskListFragment : Fragment(), ITaskItemClickListener {

    private var _binding: FragmentMainTaskListBinding? = null
    private val binding get() = _binding!!

    // Assign viewModel
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((requireActivity().application as TodoApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind newTaskButton
        binding.newTaskButton.setOnClickListener {
            // Display sheet to create new task
            NewTaskSheet(null).show(parentFragmentManager, "newTaskItem")
        }
        setRecyclerView()
    }

    private fun setRecyclerView() {
        var mainTaskListFragment = this
        taskViewModel.taskItems.observe(viewLifecycleOwner) {
            binding.todoListRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(it, mainTaskListFragment)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        // Edit task if it is not already completed
        if (!taskItem.isCompleted()) NewTaskSheet(taskItem).show(parentFragmentManager, "newTaskTag")
    }

    // Toggles task between complete and incomplete
    override fun toggleCompleteTaskItem(taskItem: TaskItem) {
        // Set task as incomplete if already completed
        if (taskItem.isCompleted()) taskViewModel.undoCompleted(taskItem)
        // Set task as complete if currently incomplete
        else taskViewModel.setCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        // Delete task
        taskViewModel.deleteTaskItem(taskItem)
    }

    override fun openTaskTagMenu(taskItem: TaskItem) {
        val tagSelectionFragment = TagSelectionFragment(taskItem)
        tagSelectionFragment.show(requireActivity().supportFragmentManager, "TagSelectionFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
