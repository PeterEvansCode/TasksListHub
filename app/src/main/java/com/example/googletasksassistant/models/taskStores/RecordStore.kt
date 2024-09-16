package com.example.googletasksassistant.models.taskStores

import com.example.googletasksassistant.models.IStandardRecord

class RecordStore<T: IStandardRecord> {

    // Private collection to store all data items
    private val _allItems = HashOnID<T>()
    private var _filteredItems: MutableList<T> = mutableListOf()
    private var _currentFilter: Regex? = Regex("")

    // Add an item to the collection
    fun add(item: T){
        //add to filtered list if the item fits the filter
        if (_currentFilter!!.containsMatchIn(item.name)) _filteredItems.add(item)
        _allItems.add(item)
    }

    fun add(items: List<T>){
        for(item in items) add(item)
    }

    // Remove an item from the collection
    fun remove(item: T){
        _filteredItems.remove(item)
        _allItems.remove(item)
    }

    // Get filtered items based on the search criteria
    fun applySearchFilter(criteria: String): List<T> {
        val pattern = ".*$criteria.*" // Regex pattern to match criteria
        _currentFilter = Regex(pattern)

        //apply filter
        _filteredItems = _allItems.values.filter { item -> _currentFilter!!.containsMatchIn(item.name) }.toMutableList()

        // Return items that match the criteria
        return _filteredItems
    }

    fun get(id: Int): T?{
        return _allItems.get(id)
    }

    fun getAll(): List<T> {
        return _allItems.values.toList()
    }

    fun getFiltered(): List<T>{
        return _filteredItems.toList()
    }
}
