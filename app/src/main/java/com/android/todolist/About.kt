package com.android.todolist

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_about.*


class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.apply {
            title = getString(R.string.about)
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        val texto = resources.getString(R.string.about_text)
        text_about.text = HtmlCompat.fromHtml(texto, HtmlCompat.FROM_HTML_MODE_LEGACY)
        text_about.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // finish()
        return true
    }

}
