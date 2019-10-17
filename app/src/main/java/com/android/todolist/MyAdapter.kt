package com.android.todolist

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

            holder.bind(context, item)
        }

        if (context is ListActivity) {
            var listaTotal = getListaItems() //(misListas[index].items + misListas[index].checks) as MutableList<String>
            val item = listaTotal[position]
            when (item) {
                in misListas[index].checks -> {
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                    holder.itemView.nombreItem.paintFlags =
                        holder.itemView.nombreItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                else -> {
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
                    holder.itemView.nombreItem.paintFlags =
                        holder.itemView.nombreItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            holder.itemView.setOnClickListener {
                if (item in misListas[index].items) {
                    listaTotal = getListaItems() // (misListas[index].items + misListas[index].checks) as MutableList<String>
                    val pos = listaTotal.indexOf(item)  // //item = listaTotal[position]
                    val to = listaTotal.lastIndex
                    for (i in pos until to) listaTotal[i] = listaTotal.set(i + 1, listaTotal[i])
                    misListas[index].items.remove(item)  // removeAt(pos)
                    misListas[index].checks.add(item)
                    notifyItemMoved(pos, to)
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)  // holder.itemView = it
                    holder.itemView.nombreItem.paintFlags =
                        holder.itemView.nombreItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tareasViewModel.updateTarea(
                        TaskEntity(misListas[index].nombre, misListas[index].items.joinToString(), misListas[index].checks.joinToString())
                    )
                } else {
                    listaTotal = getListaItems()
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
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
                    holder.itemView.nombreItem.paintFlags =
                        holder.itemView.nombreItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tareasViewModel.updateTarea(
                        TaskEntity(misListas[index].nombre, misListas[index].items.joinToString(), misListas[index].checks.joinToString())
                    )
                }
            }

            holder.bind(context, misListas[index])
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val miView = view

        fun bind(context: Context, elemento: Lista) {

            if (context is MainActivity) {
                val listaNombre = miView.nombreLista as TextView
                listaNombre.text = elemento.nombre
                if (elemento.items.isEmpty() && elemento.checks.isEmpty()) {
                    listaNombre.setTypeface(listaNombre.typeface, Typeface.ITALIC)
                } else if (elemento.items.isEmpty() && elemento.checks.isNotEmpty()) {
                    listaNombre.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                } else {
                    listaNombre.setTypeface(null, Typeface.BOLD)
                }

            } else {
                val itemNombre = miView.nombreItem as TextView
                val listaTotal = elemento.items + elemento.checks
                val elementoNombre = listaTotal[adapterPosition] //listaTotal[position]
                itemNombre.text = elementoNombre
                if (elementoNombre in elemento.checks) {
                    itemNombre.paintFlags = itemNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    itemNombre.paintFlags = itemNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                //val rv = holder.rv_items as RecyclerView
            }
        }
    }

}