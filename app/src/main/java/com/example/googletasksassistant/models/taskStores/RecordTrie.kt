package com.example.googletasksassistant.models.taskStores

import com.example.googletasksassistant.models.IStandardRecord

class RecordTrie
{/*
    class Node<T: IStandardRecord>(
        private val level: Int,
        initialData: T
    ){
        private val data: MutableList<T> by lazy { mutableListOf() }
        private val children: HashMap<Char, Node<T>> by lazy { HashMap() }

        init{
            data.add(initialData)
        }

        fun add(item: T){
            if for(dataPoint in data) add(dataPoint)
            data.add(item)

            //if the word has more letters
            if (level < item.name.length-1) {
                val nextLevel = level+1
                val keyChar = item.name[nextLevel]

                //create a new node if the corresponding node does not exist
                if (!children.containsKey(keyChar)){
                    val newNode = Node<T>(nextLevel, item)
                    children[keyChar] = newNode
                }

                //otherwise, continue adding
                else{
                    val node = children[keyChar]
                    node!!.add(item)
                }
            }
        }

        fun remove(item: T){
            data.remove(item)

        }
    }*/
}