package com.android.todolist

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TaskRepository(application: Application) {

    private val taskDao: TaskDao? = TasksDatabase.getInstance(application)?.taskDao()

    fun getTareas(): LiveData<List<TaskEntity>> {
        return taskDao?.getAllTasks() ?: MutableLiveData<List<TaskEntity>>()
    }

    /*fun orderAZTareas(): LiveData<List<TaskEntity>> {
        return taskDao?.orderAZTasks() ?: MutableLiveData<List<TaskEntity>>()
    }*/

    /*fun getTarea(name: String): LiveData<TaskEntity> {
        return taskDao?.getTaskByName(name) ?: MutableLiveData<TaskEntity>()
    }*/

    /*fun getTareaByNombre(name: String){
        return taskDao?.getTaskByName(name) ?: MutableLiveData<List<TaskEntity>>()
    }*/

    /*fun getTareaNombre(name: String) {
        if (taskDao != null) GetAsyncTask(taskDao).execute(name)
    }*/

    /*fun getTarea(name: String): TaskEntity{
        return taskDao?.getTaskByName(name) ?: TaskEntity(name, "A", "B")
    }*/

    fun insert(tarea: TaskEntity) {
        if (taskDao != null) InsertAsyncTask(taskDao).execute(tarea)
    }

    private class InsertAsyncTask(private val tareaDao: TaskDao) : AsyncTask<TaskEntity, Void, Void>() {
        override fun doInBackground(vararg tareas: TaskEntity): Void? {
            tareaDao.insert(tareas[0])
            return null
        }
    }

    /*fun insertOrder(tarea: List<TaskEntity>){
        for (tr in tarea){
            insert(tr)
        }
    }*/

    fun deleteAll() {
        if (taskDao != null) DeleteAllAsyncTask(taskDao).execute()
    }

    private class DeleteAllAsyncTask (private val tareaDao: TaskDao): AsyncTask<Void, Void, Void>() {
    //private class DeleteAllAsyncTask internal constructor(private val tareaDao: TaskDao) :
        override fun doInBackground(vararg voids: Void): Void? {
            tareaDao.deleteAll()
            return null
        }
    }

    fun delete(tarea: TaskEntity) {
        if (taskDao != null) DeleteAsyncTask(taskDao).execute(tarea)
    }

    private class DeleteAsyncTask(private val tareaDao: TaskDao): AsyncTask<TaskEntity, Void, Void>() {
        override fun doInBackground(vararg tareas: TaskEntity): Void? {
            tareaDao.delete(tareas[0])
            return null
        }
    }

    fun update(tarea: TaskEntity) {
        if (taskDao != null) UpdateAsyncTask(taskDao).execute(tarea)
    }

    private class UpdateAsyncTask(private val tareaDao: TaskDao) : AsyncTask<TaskEntity, Void, Void>() {
        override fun doInBackground(vararg tareas: TaskEntity): Void? {
            tareaDao.update(tareas[0])
            return null
        }
    }

}

/*private class InsertAsyncTask(private val tareaDao: TaskDao): AsyncTask<TaskEntity, Void, Void>() {
    override fun doInBackground(vararg tareas: TaskEntity?): Void? {
        *//*for (tarea in tareas) {
                if (tarea != null) tareaDao.insert(tarea)
            }*//*
            tareas[0]?.let { tareaDao.insert(it) }
            //tareaDao.insert(tareas[0]!!)
            return null
        }
    }*/
