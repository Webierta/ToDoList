package com.android.todolist

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*


class ListActivity : AppCompatActivity() {

    private lateinit var tareasViewModel: TaskViewModel
    private lateinit var lista: Lista
    private lateinit var listaTotal: List<String>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miAdapter: MyAdapter
    private lateinit var deleteIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        //deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp)!!
        deleteIcon = resources.getDrawable(R.drawable.ic_delete_white_24dp, null)

        tareasViewModel = run {
            ViewModelProviders.of(this).get(TaskViewModel::class.java)
        }

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
        val dividerItemDecoration = DividerItemDecoration(rv_items.context, linearLayoutManager.orientation)
        rv_items.addItemDecoration(dividerItemDecoration)
        miAdapter = MyAdapter(this, listas, indice)
        //miAdapter = MyAdapter(this, listas, indice) { partItem: String -> clickItem(partItem) }
        rv_items.adapter = miAdapter

        // Setup ItemTouchHelper
        val callback = DragManageAdapter(miAdapter, this, 0, RIGHT)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rv_items)

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
                    //tareasViewModel.saveTarea(TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString()))
                    tareasViewModel.updateTarea(
                        TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                    )
                } else {
                    Toast
                        .makeText(this, this.getString(R.string.elemento_repetido, name, "item"), Toast.LENGTH_SHORT)
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

        /*swipeRefreshItems.setColorSchemeResources(R.color.blanco)
        swipeRefreshItems.setProgressBackgroundColorSchemeResource(R.color.colorPrimary)
        var checked = false
        swipeRefreshItems.setOnRefreshListener {
            if (!checked) marcarTodo() else desmarcarTodo()
            checked = !checked
            //miAdapter = MyAdapter(this, listas, indice)
            //rv_items.adapter = miAdapter
            swipeRefreshItems.isRefreshing = false
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        this.finish()  //finish() //onBackPressed()
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
        when (item.itemId) {
            R.id.marcar_all -> marcarTodo()
            R.id.desmarcar_all -> desmarcarTodo()
            R.id.rename_list -> renameList()
            R.id.clear_list -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(R.string.limpiar_lista)
                dialogBuilder.setMessage(R.string.limpiar_lista_msg)
                dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                    lista.items.clear()
                    lista.checks.clear()
                    miAdapter.notifyDataSetChanged()
                    tareasViewModel.updateTarea(
                        TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                    )
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

    private fun marcarTodo(): Boolean {
        lista.checks.addAll(0, lista.items)  // lista.checks += lista.items
        lista.items.clear()
        miAdapter.notifyDataSetChanged()
        tareasViewModel.updateTarea(
            TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
        )
        return true
    }

    private fun desmarcarTodo(): Boolean {
        lista.items += lista.checks
        lista.checks.clear()
        miAdapter.notifyDataSetChanged()
        tareasViewModel.updateTarea(
            TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
        )
        return true
    }

    private fun renameList() {  //private fun renameList(index: Int, nombreLista: String) {
        val dialogo = EditTextDialog.newInstance(
            title = "Rename List",
            hint = lista.nombre, // nombreLista,
            layout = R.layout.dialog_new_list
        )
        dialogo.onOk = {
            val name = dialogo.editText.text.toString()
            //listaNombres.add(name)
            //val lista = Lista(nombre = name, items = mutableListOf(), checks = mutableListOf())
            if (name !in listas.map { it.nombre }) {
                //listas.add(lista)
                //notifyItemInserted(listas.indexOf(lista))
                tareasViewModel.deleteTarea(
                    TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                )
                //listas[index].nombre = name
                lista.nombre = name
                miAdapter.notifyItemChanged(listas.indexOf(lista))
                tareasViewModel.saveTarea(
                    TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                )
                supportActionBar?.title = lista.nombre
            } else {
                Toast
                    .makeText(this, this.getString(R.string.elemento_repetido, name, "list"), Toast.LENGTH_SHORT)
                    .show()
            }
            // adaptadorListas.notifyDataSetChanged()
            // mostrarListas()  //addLista(lista)
        }
        dialogo.onCancel = { Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show() }
        val ft = this.supportFragmentManager.beginTransaction()
        dialogo.show(ft, "editDescription")
    }
}
