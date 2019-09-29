package com.android.todolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Info : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        supportActionBar?.apply {
            title = getString(R.string.info)
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        // TODO: Contenido

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // finish()
        return true
    }

}