package com.example.googletasksassistant.models

class TaskTag (
    override var id: Int = 0,
    var name: String,
    var desc: String = ""
): IRecordWithID
{
}