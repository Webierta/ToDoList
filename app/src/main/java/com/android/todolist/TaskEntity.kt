package com.android.todolist

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_entity")
data class TaskEntity(

    //@PrimaryKey(autoGenerate = true)
    //var id: Int,

    //@PrimaryKey(autoGenerate = true) var id:Int = 0,
    @PrimaryKey
    @NonNull
    @ColumnInfo var nombre: String,

    @ColumnInfo
    val items: String,

    @ColumnInfo
    val checks: String
) /*{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Int = 0

}*/


