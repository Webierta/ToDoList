package com.android.todolist

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*

lateinit var lista: Lista
lateinit var adaptadorItems: ArrayAdapter<String>
lateinit var listaTotal: List<String>

class ListActivity : AppCompatActivity() {

    //lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val bundle = intent.extras
        val indice = bundle?.getInt("indice")
        lista = listas[indice!!]
        val nombreLista = lista.nombre

        supportActionBar?.apply {
            title = nombreLista
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mostrarListaItems()

        fabNewItem.setOnClickListener { view ->
            //showDialogoNewItem()
            layoutNewItem.visibility = View.VISIBLE
            newItem.requestFocus()
            inputMethodManager.showSoftInput(newItem, InputMethodManager.SHOW_IMPLICIT)
            fabNewItem.hide() //view.visibility = View.GONE
        }

        okNewItem.setOnClickListener { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            if (!newItem.text.isBlank()) {
                val name = newItem.text.toString()
                lista.items.add(name)
            }
            layoutNewItem.visibility = View.GONE
            newItem.setText("")
            fabNewItem.show()  // (fabNewItem as View).visibility = View.VISIBLE
            adaptadorItems.notifyDataSetChanged()
            mostrarListaItems()
        }

        /*val dialogo = EditTextDialog.newInstance(
            title = "Add new Item",
            hint = "Name Item",
            layout = R.layout.dialog_new_item)
        dialogo.onOk = {
            val name = dialogo.editText.text.toString()
            lista.items.add(name)
            adaptadorItems.notifyDataSetChanged()
            mostrarListaItems()
        }
        dialogo.onCancel = {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
        }
        dialogo.show(supportFragmentManager, "editDescription")*/
        //}

        listItems.setOnItemClickListener { adapterView, view, index, id ->
            val itemValue = listItems.getItemAtPosition(index) as String
            if (itemValue in lista.items) {
                val itemSelect = lista.items[index]
                lista.items.remove(lista.items[index])
                lista.checks.add(itemSelect)
            } else {
                val posicion = lista.checks.indexOf(itemValue)
                val itemSelect = lista.checks[posicion]
                lista.checks.remove(lista.checks[posicion])
                lista.items.add(itemSelect)
            }
            adaptadorItems.notifyDataSetChanged()
            mostrarListaItems()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.items_menu, menu)

        menu.getItem(0).subMenu.hasVisibleItems()
        //menu.getItem(0).subMenu.getItem(0).isVisible = true
        //menu.getItem(0).subMenu.getItem(1).isVisible = true
        //menu.getItem(0).subMenu.getItem(2).isVisible = true

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemMenu = false
        when (item.itemId) {
            R.id.marcar_all -> {
                lista.checks += lista.items
                lista.items.clear()
                itemMenu = true
            }
            R.id.desmarcar_all -> {
                lista.items += lista.checks
                lista.checks.clear()
                itemMenu = true
            }
            R.id.clear_list -> {
                lista.items.clear()
                lista.checks.clear()
                itemMenu = true
            }
        }
        if (itemMenu) {
            adaptadorItems.notifyDataSetChanged()
            mostrarListaItems()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildItem(name: String) {
        lista.items.add(name)
        adaptadorItems.notifyDataSetChanged()
        mostrarListaItems()
    }

    private fun mostrarListaItems() {
        //adaptador = ArrayAdapter<String>(this, R.layout.list_row, lista.items)
        listaTotal = lista.items + lista.checks
        adaptadorItems = ArrayAdapter(this, android.R.layout.simple_list_item_checked, listaTotal)
        listItems.adapter = adaptadorItems
        for (i in lista.items.size until listItems.adapter.count){
            listItems.setItemChecked(i, true)
        }
    }

    /*private fun showDialogoNewItem() {
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
            }
            if (!isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }*/

}
