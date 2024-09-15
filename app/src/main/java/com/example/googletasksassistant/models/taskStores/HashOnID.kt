package com.example.googletasksassistant.models.taskStores

import com.example.googletasksassistant.models.IRecordWithID

class HashOnID<T: IRecordWithID>: HashMap<Int, T>(){
    fun addRecord(item: T){
        put(item.id, item)
    }

    fun removeRecord(item: T){
        remove(item.id)
    }

    fun updateRecord(item: T){
        this[item.id] = item
    }

    fun existId(id: Int): Boolean{
        return containsKey(id)
    }
}