package com.android.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    val tareas = repository.getTareas()

    //val tareasAZ = repository.orderAZTareas()

    //fun getTareaByNombre(name: String) = repository.getTarea(name)

    fun saveTarea(tarea: TaskEntity) {
        repository.insert(tarea)
    }

    /*fun insertarLista(tarea: List<TaskEntity>){
        repository.insertOrder(tarea)
    }*/

    fun clearTabla() {
        repository.deleteAll()
    }

    fun updateTarea(tarea: TaskEntity) {
        repository.update(tarea)
    }

    fun deleteTarea(tarea: TaskEntity) {
        repository.delete(tarea)
    }
}