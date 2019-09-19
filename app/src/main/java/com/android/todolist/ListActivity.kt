package com.android.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.recyclerview_item_row.*


//lateinit var lista: Lista
//lateinit var listaTotal: List<String>

class ListActivity : AppCompatActivity() {

    private lateinit var lista: Lista
    private lateinit var listaTotal: List<String>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val bundle = intent.extras
        val indice = bundle?.getInt("indice")
        lista = listas[indice!!]
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
        miAdapter = MyAdapter(this, listas, indice)
        //miAdapter = MyAdapter(this, listas, indice) { partItem: String -> clickItem(partItem) }
        rv_items.adapter = miAdapter

        /*for (i in 0 until miAdapter.itemCount) {
            val v = cLayoutItem.getChildAt(i)
            if (v !is ViewGroup) {
                if (v is ImageView) {
                    v.setImageResource(R.drawable.ic_check_box_green_24dp)
                }
            }
        }*/

        //mostrarListaItems()

        fabNewItem.setOnClickListener {
            layoutNewItem.visibility = View.VISIBLE
            newItem.requestFocus()
            inputMethodManager.showSoftInput(newItem, InputMethodManager.SHOW_IMPLICIT)
            fabNewItem.hide() //view.visibility = View.GONE
        }

        okNewItem.setOnClickListener { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            if (!newItem.text.isBlank()) {
                val name = newItem.text.toString()
                if (name !in lista.items && name !in lista.checks) {
                    lista.items.add(name)
                    miAdapter.notifyItemInserted(lista.items.lastIndex)
                } else {
                    Toast.makeText(this, this.getString(R.string.elemento_repetido, name, "item"), Toast.LENGTH_SHORT)
                        .show()
                }
                //miAdapter.notifyDataSetChanged()
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

    /*private fun show_children(v: View) {
        val viewgroup = v as ViewGroup
        for (i in 0 until 2) {  // for (i in 0 until viewgroup.childCount) {
            val v1 = viewgroup.getChildAt(i)
            if (v1 is ViewGroup) show_children(v1)
            if (v1 is ImageView){
                v1.setImageResource(R.drawable.ic_check_box_green_24dp)
            }
        }
    }*/

    private fun clickItem(item: String) {
        if (item in lista.items) {
            val posItem = lista.items.indexOf(item) //listaTotal.indexOf(item)
            //val posCheck = listaTotal.lastIndex + 1
            lista.items.remove(item)
            lista.checks.add(item)
            miAdapter.notifyItemMoved(posItem, lista.checks.lastIndex)
        } else {
            val posCheck = lista.checks.indexOf(item)
            //val posItem = listaTotal.lastIndex + 1
            lista.checks.remove(item)
            lista.items.add(item)
            //imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            miAdapter.notifyItemMoved(posCheck, lista.items.lastIndex)
        }
        //miAdapter.notifyDataSetChanged()
        //MainActivity().updateAdapter()
    }

    override fun onSupportNavigateUp(): Boolean {
        //onBackPressed() // finish()
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        this.finish()
        //finish()
        //miAdapter.notifyDataSetChanged()
        //MainActivity.Update.actualizar()
        //MainActivity().updateAdapter()
        //    .Companion.actualizar()
        //finish()
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
