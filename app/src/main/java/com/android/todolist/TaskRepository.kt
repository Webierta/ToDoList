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

    fun insert(tarea: TaskEntity) {
        if (taskDao != null) InsertAsyncTask(taskDao).execute(tarea)
    }

    private class InsertAsyncTask(private val tareaDao: TaskDao) : AsyncTask<TaskEntity, Void, Void>() {
        override fun doInBackground(vararg tareas: TaskEntity): Void? {
            tareaDao.insert(tareas[0])
            return null
        }
    }

    fun deleteAll() {
        if (taskDao != null) DeleteAllAsyncTask(taskDao).execute()
    }

    private class DeleteAllAsyncTask(private val tareaDao: TaskDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg voids: Void): Void? {
            tareaDao.deleteAll()
            return null
        }
    }

    fun delete(tarea: TaskEntity) {
        if (taskDao != null) DeleteAsyncTask(taskDao).execute(tarea)
    }

    private class DeleteAsyncTask(private val tareaDao: TaskDao) : AsyncTask<TaskEntity, Void, Void>() {
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
