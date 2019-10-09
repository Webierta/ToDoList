package com.android.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*


class ListActivity : AppCompatActivity() {

    private lateinit var tareasViewModel: TaskViewModel
    private lateinit var lista: Lista
    private lateinit var listaTotal: List<String>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

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
        rv_items.adapter = miAdapter

        //val callback = DragManageAdapter(miAdapter, this, 0, RIGHT)
        val callback = DragManageAdapter(miAdapter, this, UP or DOWN or START or END, RIGHT)
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
                    tareasViewModel.updateTarea(
                        TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                    )
                } else {
                    Toast.makeText(this, this.getString(R.string.item_duplicate, name), Toast.LENGTH_SHORT).show()
                }
            }
            layoutNewItem.visibility = View.GONE
            newItem.setText("")
            fabNewItem.show()  // (fabNewItem as View).visibility = View.VISIBLE
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        this.finish()  // finish() //onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.items_menu, menu)
        menu.getItem(0).subMenu.hasVisibleItems()  // menu.getItem(0).subMenu.getItem(0).isVisible = true
        MenuCompat.setGroupDividerEnabled(menu, true)
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
        rv_items.adapter = miAdapter
        return true
    }

    private fun desmarcarTodo(): Boolean {
        lista.items += lista.checks
        lista.checks.clear()
        miAdapter.notifyDataSetChanged()
        tareasViewModel.updateTarea(
            TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
        )
        rv_items.adapter = miAdapter
        return true
    }

    private fun renameList() {
        val dialogo = EditTextDialog.newInstance(
            title = "Rename List",
            hint = lista.nombre, // nombreLista,
            layout = R.layout.dialog_new_list
        )
        dialogo.onOk = {
            val name = dialogo.editText.text.toString()
            if (name !in listas.map { it.nombre }) {
                tareasViewModel.deleteTarea(
                    TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                )
                lista.nombre = name
                miAdapter.notifyItemChanged(listas.indexOf(lista))
                tareasViewModel.saveTarea(
                    TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                )
                supportActionBar?.title = lista.nombre
            } else {
                Toast.makeText(this, this.getString(R.string.list_duplicate, name), Toast.LENGTH_SHORT).show()
            }
        }
        dialogo.onCancel = { Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show() }
        val ft = this.supportFragmentManager.beginTransaction()
        dialogo.show(ft, "editDescription")
    }

}
