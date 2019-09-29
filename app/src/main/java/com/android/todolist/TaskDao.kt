package com.android.todolist

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TaskDao {

    @Query("SELECT * FROM task_entity")
    //fun getAllTasks(): MutableList<TaskEntity>
    fun getAllTasks(): LiveData<List<TaskEntity>>

    //@Query("SELECT * FROM task_entity WHERE id=:id")
    //fun getById(id: Int) : TaskEntity

    //@Query("SELECT * FROM task_entity WHERE nombre = :name")
    //fun getTaskByName(name: String): TaskEntity

    @Insert
    fun insert(tarea: TaskEntity)

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //fun insertAllOrders(tarea: List<TaskEntity>)

    @Query("DELETE FROM task_entity")
    fun deleteAll()

    @Delete
    fun delete(tarea: TaskEntity)

    @Update
    fun update(tarea: TaskEntity)

    //@Query("SELECT * FROM task_entity ORDER BY nombre ASC")
    //fun orderAZTasks(): LiveData<List<TaskEntity>>

    //@Query("SELECT nombre FROM task_entity WHERE nombre = :name")
    //fun getTaskByName(name: String): String?

    //@Query("SELECT * FROM task_entity WHERE nombre = :name")
    //fun getTaskByName(name: String): TaskEntity

    //@Query("SELECT * FROM task_entity WHERE nombre = :name")
    //fun getTaskByName(name: String): LiveData<TaskEntity>

    //@Update
    //fun updateName(tarea: TaskEntity, newName: String)

    //@Query("UPDATE task_entity SET nombre= :newName WHERE nombre = :name")
    //fun setName(newName: String, name: String)

    /*@Query("SELECT * FROM task_entity WHERE nombre = :name")
    fun getTaskByName(name: String): LiveData<List<TaskEntity>>

    @Query("SELECT nombre FROM task_entity WHERE nombre = :name LIMIT 1")
    fun getTaskName(name: String): String?*/
}
