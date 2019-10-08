package com.android.todolist

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TaskDao {

    @Query("SELECT * FROM task_entity")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Insert
    fun insert(tarea: TaskEntity)

    @Query("DELETE FROM task_entity")
    fun deleteAll()

    @Delete
    fun delete(tarea: TaskEntity)

    @Update
    fun update(tarea: TaskEntity)

}
