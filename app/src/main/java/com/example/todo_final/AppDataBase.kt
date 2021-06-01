package com.example.todo_final

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo_final.DeletedTask.DeletedTask
import com.example.todo_final.DeletedTask.DeletedTaskDao
import com.example.todo_final.Task.Task
import com.example.todo_final.Task.TaskDao

@Database(entities = [Task::class, DeletedTask::class], version = 6, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao() : TaskDao
    abstract fun deletedTaskDao() : DeletedTaskDao
}