package com.example.taskslisthub.models.taskStores

import com.example.taskslisthub.models.TaskItem
import java.time.LocalDate

class TaskItemStore: RecordStore<TaskItem>() {
    companion object SortBy {
        const val DEFAULT = 0
        const val NAME = 1
        const val DATE = 2
        const val PRIORITY = 3
    }
    private var currentSortType: Int = DEFAULT

    private fun compareItems(task1: TaskItem, task2: TaskItem): Int {
        return when (currentSortType) {
            DEFAULT -> compareBy<TaskItem>({ it.dateMade}, { it.timeMade}).compare(task1, task2)
            NAME -> task1.name.compareTo(task2.name)
            DATE -> compareBy<TaskItem>({ it.formatDueDate()?: LocalDate.MAX }, { it.formatDueTime()?: LocalDate.MAX }).compare(task1, task2)
            PRIORITY -> task1.priority.compareTo(task2.priority)*-1
            else -> 0
        }
    }

    fun applySort(sortBy: Int){
        currentSortType = sortBy
        _filteredItems.sortWith { task1, task2 -> compareItems(task1, task2) }
    }

    // Add an item to the collection
    override fun add(item: TaskItem){
        if (_currentFilter!!.containsMatchIn(item.name)) {
            val index = _filteredItems.indexOfFirst { compareItems(item, it) < 0 }
            if (index >= 0) {
                _filteredItems.add(index, item)
            } else {
                _filteredItems.add(item)
            }
            _allItems.add(item)
        }
    }
}