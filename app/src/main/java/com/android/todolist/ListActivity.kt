package com.android.todolist

import android.os.Bundle
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.dialog_new_item.view.*


lateinit var lista: Lista
//var items: MutableList<String> = mutableListOf()

class ListActivity : AppCompatActivity() {

    //lateinit var lista: Lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val bundle = intent.extras
        val indice = bundle?.getInt("indice")
        lista = listas[indice!!]
        val nombreLista = lista.nombre

        for (item in lista.items) {
            val textItem = TextView(this)
            textItem.text = item
            layoutItems.addView(textItem)
        }

        supportActionBar?.apply {
            title = nombreLista
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        fabNewItem.setOnClickListener { view ->
            showDialogoNewItem()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()  // onBackPressed()
        return true
    }

    private fun showDialogoNewItem() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("New Item")

        val view = layoutInflater.inflate(R.layout.dialog_new_item, null)
        val itemEditText = view.editText_newItem as EditText
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val newItem = itemEditText.text
            var isValid = true
            if (newItem.isBlank()) {
                itemEditText.error = "Vacío"
                isValid = false
            }
            if (isValid) {
                val nameItem = itemEditText.text.toString()
                buildItem(nameItem)
                //addLista(nameList)
            }
            if (!isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun buildItem(name: String) {
        //var elementos = lista.items
        lista.items.add(name)
        addItem(name)
        //actualizaItem()
    }

    private fun addItem(name: String) {
        //val textItem = CheckedTextView(this)
        val textItem = TextView(this)
        textItem.text = name
        //textItem.isChecked = true
        layoutItems.addView(textItem)
    }

    private fun actualizaItem(){
        for (item in lista.items) {
            //println(item)
            val textItem = TextView(this)
            textItem.text = item
            layoutItems.addView(textItem)
        }
    }

}
