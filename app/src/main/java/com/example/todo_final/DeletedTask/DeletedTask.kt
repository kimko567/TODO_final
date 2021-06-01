package com.example.todo_final.DeletedTask

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName="Deleted_Tasks")
data class DeletedTask(@PrimaryKey var id: Int, var creationDate: String ) {
    var deletionDate =  SimpleDateFormat("dd/M/yyyy").format(Date()).toString()
}