package com.example.googletasksassistant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googletasksassistant.TagSelectionFragment.TagSelectionFragment
import com.example.googletasksassistant.databinding.FragmentMainTaskListBinding
import com.example.googletasksassistant.models.TaskItem

class MainTaskListFragment : Fragment(), ITaskItemClickListener {

    private lateinit var binding: FragmentMainTaskListBinding

    // Assign viewModel
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((requireActivity().application as TodoApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind newTaskButton
        binding.newTaskButton.setOnClickListener {
            // Display sheet to create new task
            NewTaskSheet(null).show(parentFragmentManager, "newTaskItem")
        }

        // Bind toolbar with activity's drawer layout
        (activity as? AppCompatActivity)?.let { appCompatActivity ->
            val drawerLayout = appCompatActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            binding.taskToolbar.bindWithDrawerLayout(appCompatActivity, drawerLayout)
        }

        setRecyclerView()
        setSearchBar()
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

    private fun setSearchBar(){
        // search bar
        binding.taskSearchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                taskViewModel.searchForTask(binding.taskSearchBar.text.toString())
            }

            //necessary overrides (no functionality)
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
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
        binding.taskToolbar.cleanUp()
    }
}
