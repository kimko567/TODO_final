package com.example.todo_final.Task

import androidx.room.*
import com.example.todo_final.Task.Task

@Dao
interface TaskDao{

    @Query("SELECT * FROM Tasks")
    fun getAll(): Array<Task>

    @Query("SELECT * FROM Tasks WHERE ID=:id")
    fun get(id: Int): Task

    @Query("SELECT * FROM Tasks WHERE body LIKE '%' || :search || '%'")
    fun getContains(search: String): Array<Task>

    @Insert
    fun insertItem(task: Task)

    @Update
    fun updateItem(task: Task)

    @Delete()
    fun deleteItem(task: Task)

}