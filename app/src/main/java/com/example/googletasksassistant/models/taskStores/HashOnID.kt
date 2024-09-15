package com.example.googletasksassistant.models.taskStores

import com.example.googletasksassistant.models.IRecordWithID

class HashOnID<T: IRecordWithID>: HashMap<Int, T>(){
    fun add(item: T){
        put(item.id, item)
    }

    fun remove(item: T){
        remove(item.id)
    }

    fun update(item: T){
        this[item.id] = item
    }

    fun existId(id: Int): Boolean{
        return containsKey(id)
    }
}