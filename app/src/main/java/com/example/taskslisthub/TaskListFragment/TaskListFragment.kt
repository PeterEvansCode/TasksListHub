package com.example.taskslisthub.TaskListFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskslisthub.R
import com.example.taskslisthub.TagSelectionFragment.TagSelectionFragment
import com.example.taskslisthub.TasksListHub
import com.example.taskslisthub.databinding.FragmentMainTaskListBinding
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import com.example.taskslisthub.models.taskStores.TaskItemStore

class TaskListFragment(
    private val tagFilter: TaskTag? = null,
    private val priorityFilter: Int = -1
) : Fragment(), ITaskItemClickListener {
    private var sortMethod: Int = 0
    private lateinit var binding: FragmentMainTaskListBinding

    // Assign viewModel
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((requireActivity().application as TasksListHub).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialise google tasks API
        taskViewModel.setGoogleAccount(requireContext())

        //initialise binding
        binding = FragmentMainTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind newTaskButton
        binding.newTaskButton.setOnClickListener {
            // Display sheet to create new task
            NewTaskSheet(taskTag = tagFilter).show(parentFragmentManager, "newTaskItem")
        }

        // Bind toolbar with activity's drawer layout
        (activity as? AppCompatActivity)?.let { appCompatActivity ->
            val drawerLayout = appCompatActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            binding.taskDrawerButton.bindWithDrawerLayout(appCompatActivity, drawerLayout)
        }

        setRecyclerView()
        setSearchBar()
        setSortButton()
    }

    private fun setRecyclerView() {
        val mainTaskListFragment = this
        taskViewModel.taskItems.observe(viewLifecycleOwner) {

            //sort tags first by completed, then by given sort
            val incompleteTasks = it.filter { taskItem ->  !taskItem.isCompleted()}
            val completedTasks = it.filter { taskItem ->  taskItem.isCompleted()}
            var tasksToDisplay = incompleteTasks + completedTasks

            //if inside a tag folder, only show tasks relating to that tag
            if (tagFilter != null){
                tasksToDisplay = it.filter { taskItem -> taskItem.tags.containsKey(tagFilter.id) }
            }
            else if (priorityFilter >= 0) {
                tasksToDisplay = it.filter { item -> item.priority == priorityFilter }
            }

            //display tasks
            binding.todoListRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(tasksToDisplay, mainTaskListFragment)
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

    private fun setSortButton(){
        binding.sortButton.setOnClickListener {
            // Create a PopupMenu
            val popupMenu = PopupMenu(requireContext(), binding.sortButton)
            // Inflate the menu resource
            popupMenu.menuInflater.inflate(R.menu.sort_popup, popupMenu.menu)
            // Set a click listener for menu items
            popupMenu.setOnMenuItemClickListener { item ->
                var returnVal = false
                when (item.itemId) {
                    R.id.sort_custom -> {
                        // Handle edit action
                        sortMethod = TaskItemStore.CUSTOM
                        returnVal = true
                    }

                    R.id.sort_date -> {
                        // Handle edit action
                        sortMethod = TaskItemStore.DATE
                        returnVal = true
                    }

                    R.id.sort_name -> {
                        // Handle edit action
                        sortMethod = TaskItemStore.NAME
                        returnVal = true
                    }

                    R.id.sort_priority -> {
                        // Handle edit action
                        sortMethod = TaskItemStore.PRIORITY
                        returnVal = true
                    }

                    else -> returnVal = false
                }
                taskViewModel.applyTaskSort(sortMethod)
                returnVal
            }
            // Show the menu
            popupMenu.show()
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        // Edit task if it is not already completed
        if (!taskItem.isCompleted()) NewTaskSheet(taskItem, tagFilter).show(parentFragmentManager, "newTaskTag")
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
        binding.taskDrawerButton.cleanUp()
    }
}
