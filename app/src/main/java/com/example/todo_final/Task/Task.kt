package com.example.todo_final.Task

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.reflect.Constructor
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "Tasks")
data class Task(var body: String) {
    constructor(id: Int, body: String) : this(body) {
        this.id = id
    }

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var date: String =  SimpleDateFormat("dd/M/yyyy").format(Date()).toString()


}