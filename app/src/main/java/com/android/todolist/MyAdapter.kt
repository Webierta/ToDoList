package com.android.todolist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*

class MyAdapter(
    private val context: Context,
    private val misListas: MutableList<Lista> = ArrayList(),
    val index: Int
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private lateinit var tareasViewModel: TaskViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (context is MainActivity) R.layout.recyclerview_list_row else R.layout.recyclerview_item_row
        val itemView = layoutInflater.inflate(layout, parent, false)
        tareasViewModel = run {
            ViewModelProviders.of(context as AppCompatActivity).get(TaskViewModel::class.java)
        }
        return MyAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (context is MainActivity) misListas.size else misListas[index].items.size + misListas[index].checks.size
    }

    fun getListaItems(): MutableList<String> {
        return (misListas[index].items + misListas[index].checks) as MutableList<String>
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {

        if (context is MainActivity) {
            val item = misListas[position]
            when {
                item.items.size < 1 && item.checks.size < 1 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_playlist_add_black_24dp)
                item.items.size < 1 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_done_all_green_24dp)
                item.items.size > 0 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_priority_high_red_24dp)
            }

            holder.itemView.setOnClickListener {
                val pos = misListas.indexOf(item)
                Toast.makeText(context, misListas[pos].nombre, Toast.LENGTH_SHORT).show()
                val toActivity = Intent(context, ListActivity::class.java)
                toActivity.putExtra("indice", pos)  // position
                context.startActivity(toActivity)
            }

            /*holder.itemView.setOnLongClickListener {
                //val listaSelect = misListas[position]
                val pos = misListas.indexOf(item)
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.popup_menu_list, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.rename -> {
                            Toast.makeText(context, "You Clicked : " + item.nombre, Toast.LENGTH_SHORT).show()
                            renameList(pos, item.nombre)
                        }
                        R.id.remove -> {
                            Toast.makeText(context, "You Clicked : " + menuItem.title, Toast.LENGTH_SHORT).show()
                            val dialogBuilder = AlertDialog.Builder(context)
                            dialogBuilder.setTitle(R.string.delete_lista)
                            dialogBuilder.setMessage(R.string.delete_lista_msg)
                            dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                                tareasViewModel.deleteTarea(
                                    TaskEntity(
                                        misListas[pos].nombre,
                                        misListas[pos].items.joinToString(),
                                        misListas[pos].checks.joinToString()
                                    )
                                )
                                misListas.removeAt(pos) // remove(misListas[position])
                                notifyItemRemoved(pos)
                            }
                            dialogBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                dialog.cancel()
                            }
                            dialogBuilder.show()
                            //misListas.removeAt(pos) // remove(misListas[position])
                            //notifyItemRemoved(pos)
                        }
                    }
                    true
                }
                popupMenu.show()
                *//*Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item.nombre, "list"),
                    Toast.LENGTH_SHORT
                ).show()
                val pos = misListas.indexOf(item)  //val pos = listas.indexOf(item)
                misListas.remove(item) // remove(misListas[position])
                notifyItemRemoved(pos)*//*
                true
            }*/
            holder.bind(context, item)  // holder.bind(context, item, clickListener)
        }

        if (context is ListActivity) {
            var listaTotal = getListaItems() //(misListas[index].items + misListas[index].checks) as MutableList<String>
            val item = listaTotal[position]
            when (item) {
                in misListas[index].checks -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                else -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            }

            holder.itemView.setOnClickListener {
                if (item in misListas[index].items) {
                    listaTotal = getListaItems() // (misListas[index].items + misListas[index].checks) as MutableList<String>
                    //item = listaTotal[position]
                    val pos = listaTotal.indexOf(item)
                    val to = listaTotal.lastIndex
                    for (i in pos until to) listaTotal[i] = listaTotal.set(i + 1, listaTotal[i])
                    misListas[index].items.remove(item)  // removeAt(pos)
                    misListas[index].checks.add(item)
                    notifyItemMoved(pos, to)
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)  // holder.itemView = it
                    tareasViewModel.updateTarea(
                        TaskEntity(misListas[index].nombre, misListas[index].items.joinToString(), misListas[index].checks.joinToString())
                    )
                } else {
                    listaTotal = getListaItems()
                    //item = listaTotal[position]
                    val pos = listaTotal.indexOf(item)
                    val to = 0  // // if (misListas[index].items.isEmpty()) 0 else misListas[index].items.lastIndex + 1
                    println("$item en $pos")
                    if (pos != 0) {
                        for (i in pos..to + 1) listaTotal[i] = listaTotal.set(i - 1, listaTotal[i])
                    } else {
                        for (i in pos until to) listaTotal[i] = listaTotal.set(i + 1, listaTotal[i])
                    }
                    misListas[index].checks.remove(item)  // removeAt(pos)
                    misListas[index].items.add(0, item)
                    notifyItemMoved(pos, 0)
                    // UPDATE LISTAS
                    /*if (pos < to) {
                        for (i in pos until to) {
                            //misListas[i] = misListas.set(i+1, misListas[i])
                            misListas[index].items[i] = misListas[index].items.set(i+1, misListas[index].items[i])
                        }
                    } else {
                        for (i in pos..to + 1) {
                            //misListas[i] = misListas.set(i-1, misListas[i])
                            misListas[index].items[i] = misListas[index].items.set(i-1 , misListas[index].checks[i])
                            //misListas[index].checks[i] = misListas[index].checks.set(i-1 , misListas[index].items[i])
                        }
                    }*/
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
                    tareasViewModel.updateTarea(
                        TaskEntity(misListas[index].nombre, misListas[index].items.joinToString(), misListas[index].checks.joinToString())
                    )
                }
            }

            /*holder.itemView.setOnLongClickListener {
                //listaTotal = getListaItems() // (misListas[index].items + misListas[index].checks) as MutableList<String>
                // val item = listaTotal[position]
                //val pos = listaTotal.indexOf(item) // pos = misListas[index].items/checks.indexOf(item)
                val pos = getListaItems().indexOf(item)
                Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item, "item"),
                    Toast.LENGTH_SHORT
                ).show()
                // lista.checks.remove(item)
                if (item in misListas[index].items) misListas[index].items.remove(item) else misListas[index].checks.remove(item)
                notifyItemRemoved(pos)  // position
                tareasViewModel.updateTarea(
                    TaskEntity(
                        misListas[index].nombre,
                        misListas[index].items.joinToString(),
                        misListas[index].checks.joinToString()
                    )
                )
                *//*listaTotal = getListaItems()  // (misListas[index].items + misListas[index].checks) as MutableList<String>
                // CONTROL UPDATE DATA
                for ((index, cadaItem) in listaTotal.withIndex()){
                    println("""
                    |$index $cadaItem""".trimMargin())
                }*//*
                true
            }*/

            holder.bind(context, misListas[index])  // holder.bind(context, misListas[index], clickListener)
            //holder.update(item, misListas[index])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val miView = view

        //fun bind(context: Context, elemento: Lista, clickListener: (String) -> Unit) {
        fun bind(context: Context, elemento: Lista) {
            if (context is MainActivity) {
                val listaNombre = miView.nombreLista as TextView
                listaNombre.text = elemento.nombre
                //itemView.setOnClickListener { clickListener(elemento.nombre) }
            } else {
                val itemNombre = miView.nombreItem as TextView
                val listaTotal = elemento.items + elemento.checks
                val elementoNombre = listaTotal[adapterPosition] //listaTotal[position]
                itemNombre.text = elementoNombre
                //itemView.setOnClickListener { clickListener(elementoNombre) }
                //val rv = holder.rv_items as RecyclerView
            }
        }
    }
}