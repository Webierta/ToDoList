package com.android.todolist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

data class Lista(
    val nombre: String,
    val items: MutableList<String>,
    val checks: MutableList<String>
)

var listas: MutableList<Lista> = mutableListOf()

//class MainActivity : AppCompatActivity() {
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.app_name, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //toggle.isDrawerIndicatorEnabled = true

        navView.setNavigationItemSelectedListener(this)

        fab.setOnClickListener {
            addNewList()  // showDialogoNewList()
        }

        // TEST LISTA
        val listaTest = Lista(
            nombre = "Test",
            items = mutableListOf("Abiu", "Batuan", "Black Mulberry", "Cape Gooseberry", "Desert banana"),
            //"Abiu2", "Batuan2", "Black Mulberry2", "Cape Gooseberry2", "Desert banana2",
            //"Abiu3", "Batuan3", "Black Mulberry3", "Cape Gooseberry3", "Desert banana3"),
            checks = mutableListOf()
        )
        listas.add(listaTest)
        addLista(listaTest)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newList -> {
                addNewList()
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
            val lista = Lista(nombre = name, items = mutableListOf(), checks = mutableListOf())
            listas.add(lista)
            addLista(lista)
        }
        dialogo.onCancel = {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
        }
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

        /*
        return when (item.itemId) {
            R.id.action_about -> true
            else -> super.onOptionsItemSelected(item)
        }*/
        return when (item.itemId) {
            R.id.action_about -> {
                val toAbout = Intent(this, About::class.java)
                startActivity(toAbout)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addLista(lista: Lista) {
        // creating TextView programmatically
        val textLista = TextView(this)
        textLista.textSize = 20f
        textLista.setPadding(0, 0, 0, 30)
        textLista.text = lista.nombre
        textLista.setOnClickListener {
            //Toast.makeText(this, "Creada nueva lista $lista.nombre", Toast.LENGTH_SHORT).show()
            //val lista = Lista.nombre
            val index = listas.indexOf(lista)
            //Toast.makeText(this, "$index", Toast.LENGTH_SHORT).show()
            val toActivity = Intent(this, ListActivity::class.java)
            toActivity.putExtra("indice", index)
            startActivity(toActivity)
        }
        // add TextView to LinearLayout
        layoutListas.addView(textLista)
    }

    /*private fun showDialogoNewList() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("New List")
        //builder.setTitle(getString(R.string.new_list))

        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        // Seems ok to inflate view with null rootView
        val view = layoutInflater.inflate(R.layout.dialog_new_list, null)
        //val listEditText = view.findViewById(R.id.editText_newList) as EditText
        val listEditText = view.editText_newList as EditText
        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val newList = listEditText.text
            var isValid = true
            if (newList.isBlank()) {
                listEditText.error = "Vacío"
                isValid = false
            }
            if (isValid) {
                // do something
                //var name_list = listEditText.text.toString()
                val nameList = listEditText.text.toString()
                buildList(nameList)
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

    private fun buildList(name: String) {
        //Toast.makeText(this, "Creada nueva lista $name", Toast.LENGTH_SHORT).show()
        //Snackbar.make(fab, "Creada nueva lista $name", Snackbar.LENGTH_LONG)
        //    .setAction("Action", null).show()
        val lista = Lista(nombre = name, items = mutableListOf(), checks = mutableListOf())
        listas.add(lista)
        addLista(lista)
    }*/

}
