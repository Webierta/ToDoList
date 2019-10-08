package com.android.todolist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


data class Lista(var nombre: String, val items: MutableList<String>, val checks: MutableList<String>)

var listas = mutableListOf<Lista>()

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tareasViewModel: TaskViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var miAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tareasViewModel = run {
            ViewModelProviders.of(this).get(TaskViewModel::class.java)
        }
        addObserver()

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        linearLayoutManager = LinearLayoutManager(this)
        rv_listas.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(rv_listas.context, linearLayoutManager.orientation)
        rv_listas.addItemDecoration(dividerItemDecoration)
        miAdapter = MyAdapter(this, listas, 0)
        rv_listas.adapter = miAdapter

        val callback = DragManageAdapter(miAdapter, this, UP or DOWN or START or END, RIGHT)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rv_listas)

        fab.setOnClickListener {
            addNewList()
        }
    }

    private fun addObserver() {
        val observer = Observer<List<TaskEntity>> { tareas ->
            if (tareas != null) for (tarea in tareas) {
                val listaItems =
                    if (tarea.items.isEmpty()) mutableListOf() else tarea.items.split(",").map { it.trim() } as MutableList<String>
                val listaCheck =
                    if (tarea.checks.isEmpty()) mutableListOf() else tarea.checks.split(",").map { it.trim() } as MutableList<String>
                val listaTest = Lista(nombre = tarea.nombre, items = listaItems, checks = listaCheck)
                if (listaTest.nombre !in listas.map { it.nombre }) {
                    val lastIndex = listas.lastIndex
                    listas.add(listaTest)
                    miAdapter.notifyItemInserted(lastIndex + 1)
                }
            }
        }
        tareasViewModel.tareas.observe(this, observer)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newList -> addNewList()
            R.id.sortDone -> ordenarDone()
            R.id.sortAToZ -> ordenarAZ()
            R.id.removeAllList -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(R.string.delete_listas)
                dialogBuilder.setMessage(R.string.delete_listas_msg)
                dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                    listas.clear()
                    miAdapter.notifyDataSetChanged()
                    tareasViewModel.clearTabla()
                }
                dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                dialogBuilder.show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun addNewList() {
        val dialogo = EditTextDialog.newInstance(
            title = getString(R.string.add_new_list),
            hint = getString(R.string.name_list),
            layout = R.layout.dialog_new_list
        )
        dialogo.onOk = {
            val name = dialogo.editText.text.toString()
            val lista = Lista(nombre = name, items = mutableListOf(), checks = mutableListOf())
            if (name !in listas.map { it.nombre }) {
                listas.add(lista)
                miAdapter.notifyItemInserted(listas.indexOf(lista))
                tareasViewModel.saveTarea(
                    TaskEntity(lista.nombre, lista.items.joinToString(), lista.checks.joinToString())
                )
            } else {
                Toast.makeText(this, this.getString(R.string.list_duplicate, name), Toast.LENGTH_SHORT).show()
            }
        }
        dialogo.onCancel = { Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show() }
        dialogo.show(supportFragmentManager, "editDescription")
    }

    private fun ordenarDone() {
        val listasVacias = mutableListOf<Lista>()
        val listasNoDone = mutableListOf<Lista>()
        val listasHechas = mutableListOf<Lista>()
        for (ls in listas) when {
            ls.items.isEmpty() && ls.checks.isEmpty() -> listasVacias.add(ls)
            ls.items.isNotEmpty() -> listasNoDone.add(ls)
            else -> listasHechas.add(ls)
        }
        val listasSortDone = (listasNoDone + listasVacias + listasHechas) as MutableList<Lista>
        listas.clear()
        miAdapter.notifyDataSetChanged()
        tareasViewModel.clearTabla()
        for (listaDone in listasSortDone) {
            listas.add(listaDone)
            miAdapter.notifyItemInserted(listas.indexOf(listaDone))
            tareasViewModel.saveTarea(
                TaskEntity(listaDone.nombre, listaDone.items.joinToString(), listaDone.checks.joinToString())
            )
        }
        rv_listas.adapter = miAdapter
    }

    private fun ordenarAZ() {
        val listasAZ = listas.sortedBy { it.nombre } as MutableList<Lista>
        listas.clear()
        miAdapter.notifyDataSetChanged()
        tareasViewModel.clearTabla()
        for (listaAZ in listasAZ) {
            listas.add(listaAZ)
            miAdapter.notifyItemInserted(listas.indexOf(listaAZ))
            tareasViewModel.saveTarea(
                TaskEntity(listaAZ.nombre, listaAZ.items.joinToString(), listaAZ.checks.joinToString())
            )
        }
        rv_listas.adapter = miAdapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.getItem(0).subMenu.hasVisibleItems()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                val toInfo = Intent(this, Info::class.java)
                startActivity(toInfo)
                true
            }
            R.id.action_about -> {
                val toAbout = Intent(this, About::class.java)
                startActivity(toAbout)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
