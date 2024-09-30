package com.example.taskslisthub.models

class TaskTag (
    override var id: Int = 0,
    override var name: String,
    var desc: String = ""
): IStandardRecord
{
}