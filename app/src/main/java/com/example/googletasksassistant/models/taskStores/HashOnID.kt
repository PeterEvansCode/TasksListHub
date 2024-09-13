package com.example.googletasksassistant.models.taskStores

import com.example.googletasksassistant.models.TaskItem

class HashOnID {
    val taskItems = HashMap<Int, TaskItem>()

    fun add(taskItem: TaskItem){
        taskItems.put(taskItem.id, taskItem)
    }

    fun remove(taskItem: TaskItem){
        taskItems.remove(taskItem.id)
    }

    fun update(taskItem: TaskItem){
        taskItems[taskItem.id] = taskItem
    }

    fun get(taskID: Int): TaskItem{
        return taskItems[taskID]!!
    }
}