package com.example.todo_final.DeletedTask

import androidx.room.*
import com.example.todo_final.DeletedTask.DeletedTask

@Dao
interface DeletedTaskDao {
    @Query("SELECT * FROM Deleted_Tasks")
    fun getAll(): Array<DeletedTask>

    @Insert
    fun insert(deletedTask: DeletedTask)
}