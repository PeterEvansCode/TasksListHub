package com.example.googletasksassistant

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googletasksassistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TaskItemClickListener
{
    private lateinit var binding : ActivityMainBinding
    //assign viewModel
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //set view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bind newTaskButton
        binding.newTaskButton.setOnClickListener{
            //display sheet to create new task
            NewTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }
        setRecyclerView()
    }

    private fun setRecyclerView(){
        val mainActivity = this
        taskViewModel.taskItems.observe(this){
            binding.todoListRecyclerView.apply{
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, mainActivity)

            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        //edit task if it is not already completed
        if(!taskItem.isCompleted()) NewTaskSheet(taskItem).show(supportFragmentManager, "newTaskTag")
    }

    //toggles task between complete and incomplete
    override fun toggleCompleteTaskItem(taskItem: TaskItem) {
        //set task as incomplete if already completed
        if(taskItem.isCompleted()) taskViewModel.undoCompleted(taskItem)
        //set task as complete if currently incomplete
        else taskViewModel.setCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        //delete task
        taskViewModel.deleteTaskItem(taskItem)
    }
}