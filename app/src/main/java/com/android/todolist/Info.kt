package com.android.todolist

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_info.*


class Info : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        supportActionBar?.apply {
            title = getString(R.string.info)
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        val texto = resources.getString(R.string.info_text)
        text_info.text = HtmlCompat.fromHtml(texto, HtmlCompat.FROM_HTML_MODE_LEGACY)
        text_info.movementMethod = ScrollingMovementMethod()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // finish()
        return true
    }

}