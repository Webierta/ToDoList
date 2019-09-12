package com.android.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*


lateinit var lista: Lista
//lateinit var adaptadorItems: ArrayAdapter<String>
lateinit var listaTotal: List<String>
//var listaTotal: List<String> = mutableListOf()
//var listaTotal: List<MutableList<String>> = mutableListOf()
//var listaTotal: MutableList<String> = mutableListOf<String>()

class ListActivity : AppCompatActivity() {

    //val mainActivity: MainActivity? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miAdapter: RecyclerAdapterItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val bundle = intent.extras
        val indice = bundle?.getInt("indice")
        //val getNombre = bundle?.getString("nombreLista")
        //val getNombre = intent.getStringExtra("nombreLista")

        lista = listas[indice!!]
        //val nombreLista = lista.nombre
        //listaTotal = (lista.items + lista.checks) as MutableList<String>
        listaTotal = lista.items + lista.checks

        supportActionBar?.apply {
            title = lista.nombre
            subtitle = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(true)
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        linearLayoutManager = LinearLayoutManager(this)
        rv_items.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            rv_items.context,
            linearLayoutManager.orientation
        )
        rv_items.addItemDecoration(dividerItemDecoration)

        //miAdapter = RecyclerAdapterItem(this, listas, indice)
        miAdapter = RecyclerAdapterItem(this, listas, indice) { partItem: String -> clickItem(partItem) }
        rv_items.adapter = miAdapter

        //mostrarListaItems()

        fabNewItem.setOnClickListener {
            layoutNewItem.visibility = View.VISIBLE
            newItem.requestFocus()
            inputMethodManager.showSoftInput(newItem, InputMethodManager.SHOW_IMPLICIT)
            fabNewItem.hide() //view.visibility = View.GONE
        }

        okNewItem.setOnClickListener { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (!newItem.text.isBlank()) {  // y no está repetido en esa lista
                val name = newItem.text.toString()
                lista.items.add(name)
                miAdapter.notifyItemInserted(lista.items.indexOf(name))
            }
            layoutNewItem.visibility = View.GONE
            newItem.setText("")
            fabNewItem.show()  // (fabNewItem as View).visibility = View.VISIBLE
            //adaptadorItems.notifyDataSetChanged()
            //mostrarListaItems()
        }

        //listItems.isLongClickable = true
        /*listItems.setOnItemLongClickListener { _, _, index, _ ->
            val itemValue = listItems.getItemAtPosition(index) as String
            if (itemValue in lista.items) {
                lista.items.remove(itemValue)
            } else {
                lista.checks.remove(itemValue)
            }
            adaptadorItems.notifyDataSetChanged()
            mostrarListaItems()
            true
        }

        listItems.setOnItemClickListener { _, _, index, _ ->
            val itemValue = listItems.getItemAtPosition(index) as String
            if (itemValue in lista.items) {
                val itemSelect = lista.items[index]
                lista.items.remove(lista.items[index])
                //miAdapter.notifyItemRemoved(index)
                lista.checks.add(itemSelect)
                //miAdapter.notifyItemInserted(lista.checks.indexOf(itemSelect))

            } else {
                val posicion = lista.checks.indexOf(itemValue)
                val itemSelect = lista.checks[posicion]
                lista.checks.remove(lista.checks[posicion])
                //miAdapter.notifyItemRemoved(posicion)
                lista.items.add(itemSelect)
                //miAdapter.notifyItemInserted(lista.checks.indexOf(itemSelect))
            }
            //adaptadorItems.notifyDataSetChanged()
            //mostrarListaItems()
            miAdapter.notifyDataSetChanged()
        }*/
    }

    private fun clickItem(item: String) {
        if (item in lista.items) {
            lista.items.remove(item)
            lista.checks.add(item)
        } else {
            lista.checks.remove(item)
            lista.items.add(item)
        }
        miAdapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        //onBackPressed() finish()
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        this.finish()
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
        fun updateItemMenu() {
            //adaptadorItems.notifyDataSetChanged()
            //mostrarListaItems()
            miAdapter.notifyDataSetChanged()
        }
        when (item.itemId) {
            R.id.marcar_all -> {
                lista.checks += lista.items
                lista.items.clear()
                updateItemMenu()
                return true

            }
            R.id.desmarcar_all -> {
                lista.items += lista.checks
                lista.checks.clear()
                updateItemMenu()
                return true
            }
            R.id.clear_list -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(R.string.limpiar_lista)
                dialogBuilder.setMessage(R.string.limpiar_lista_msg)
                dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                    lista.items.clear()
                    lista.checks.clear()
                    updateItemMenu()
                }
                dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                dialogBuilder.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*private fun mostrarListaItems() {
        //adaptador = ArrayAdapter<String>(this, R.layout.list_row, lista.items)

        listaTotal = lista.items + lista.checks
        adaptadorItems = ArrayAdapter(this, android.R.layout.simple_list_item_checked, listaTotal)
        listItems.adapter = adaptadorItems
        for (i in lista.items.size until listItems.adapter.count) {
            listItems.setItemChecked(i, true)
        }
    }*/
}
