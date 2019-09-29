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
        //addContact_button.setOnClickListener { addContact() }

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        linearLayoutManager = LinearLayoutManager(this)
        rv_listas.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            rv_listas.context,
            linearLayoutManager.orientation
        )
        rv_listas.addItemDecoration(dividerItemDecoration)
        //miAdapter = RecyclerAdapter(this, listas)
        miAdapter = MyAdapter(this, listas, 0)
        //miAdapter = MyAdapter(this, listas, 0) { lista: String -> clickItem(lista) }
        rv_listas.adapter = miAdapter
        //mostrarListas()

        fab.setOnClickListener {
            addNewList()  // showDialogoNewList()
        }

        /*listListas.setOnItemLongClickListener { adapterView, view, index, id ->
            Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show()
            // dialogo de confirmación
            // OK -> eliminar lista
            listas.removeAt(index)
            adaptadorListas.notifyDataSetChanged()
            mostrarListas()
            //val listValue = listListas.getItemAtPosition(index) as String
            //listaNombres.remove(listValue)
            true
        }

        listListas.setOnItemClickListener { adapterView, view, index, id ->
            //val listValue = listListas.getItemAtPosition(index) as String
            //val lista = Lista.nombre
            //val indice = listas.indexOf(listValue)
            if (listListas.isItemChecked(index)) {
                listListas.setItemChecked(index, false)
            } else {
                listListas.setItemChecked(index, true)
            }
            val toActivity = Intent(this, ListActivity::class.java)
            toActivity.putExtra("indice", index)
            startActivity(toActivity)
        }*/
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

    /*private fun clickItem(item: String) {
        val listaNombres = listas.map { it.nombre }
        val posicion = listaNombres.indexOf(item)
        //Toast.makeText(this, listas[posicion].nombre, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, listaNombres[posicion], Toast.LENGTH_SHORT).show()
        val toActivity = Intent(this, ListActivity::class.java)
        toActivity.putExtra("indice", posicion)
        this.startActivity(toActivity)
    }*/

    /*private fun listaItemClicked(lista : Lista) {
        Toast.makeText(this, "Clicked: ${lista.nombre}", Toast.LENGTH_LONG).show()
        //val index = listas.indexOf(lista.nombre)
        val toActivity = Intent(this, ListActivity::class.java)
        toActivity.putExtra("indice", index)
        this.startActivity(toActivity)
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.newList -> {
                addNewList()
            }

            R.id.sortDone -> {
                // TODO: sort by done
            }

            R.id.sortAToZ -> {
                val listasAZ = listas.sortedBy { it.nombre } as MutableList<Lista>
                listas.clear()
                miAdapter.notifyDataSetChanged()
                tareasViewModel.clearTabla()

                for (listaAZ in listasAZ) {
                    listas.add(listaAZ)
                    miAdapter.notifyItemInserted(listas.indexOf(listaAZ))
                    tareasViewModel.saveTarea(TaskEntity(
                        listaAZ.nombre,
                        listaAZ.items.joinToString(),
                        listaAZ.checks.joinToString()
                    ))
                }
                rv_listas.adapter = miAdapter

                /*val intent = intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)*/
            }

            R.id.removeAllList -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(R.string.delete_listas)
                dialogBuilder.setMessage(R.string.delete_listas_msg)
                dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                    listas.clear()
                    miAdapter.notifyDataSetChanged()  // updateItemMenu()
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
            title = "Add new List",
            hint = "Name List",
            layout = R.layout.dialog_new_list
        )
        dialogo.onOk = {
            val name = dialogo.editText.text.toString()
            val lista = Lista(
                nombre = name,
                items = mutableListOf(),
                checks = mutableListOf()
            )
            if (name !in listas.map { it.nombre }) {
                listas.add(lista)
                miAdapter.notifyItemInserted(listas.indexOf(lista))
                tareasViewModel.saveTarea(
                    TaskEntity(
                        lista.nombre,
                        lista.items.joinToString(),
                        lista.checks.joinToString()
                    )
                )
            } else {
                Toast.makeText(this, this.getString(R.string.elemento_repetido, name, "list"), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialogo.onCancel = { Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show() }
        dialogo.show(supportFragmentManager, "editDescription")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    //object UpdateListas{
/*    fun mostrarListas() {
        //val listaNombres = listas.map { it.nombre }
        //adaptadorListas = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista.)

        val listaNombres = listas.map { it.nombre }
        adaptadorListas = ArrayAdapter(this, android.R.layout.simple_list_item_checked, listaNombres)
        listListas.adapter = adaptadorListas

        for ((i, cadaLista) in listas.withIndex()) {
            if (cadaLista.checks.size > 0 && cadaLista.items.size < 1) {
                listListas.setItemChecked(i, true)
            }
        }
    }*/
}
