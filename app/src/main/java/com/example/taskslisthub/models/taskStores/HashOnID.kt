package com.example.taskslisthub.models.taskStores

import com.example.taskslisthub.models.IStandardRecord

class HashOnID<T: IStandardRecord>: HashMap<Int, T>(){
    fun add(item: T){
        put(item.id, item)
    }

    fun add(items: List<T>){
        for (item in items) add(item)
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