package com.android.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*

class MyAdapter(
    private val context: Context,
    private val misListas: MutableList<Lista> = ArrayList(),
    private val index: Int,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (context is MainActivity) R.layout.recyclerview_list_row else R.layout.recyclerview_item_row
        val itemView = layoutInflater.inflate(layout, parent, false)
        return MyAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (context is MainActivity) misListas.size else misListas[index].items.size + misListas[index].checks.size
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        if (context is MainActivity) {
            val item = misListas[position]
            /*holder.itemView.setOnClickListener {
                Toast.makeText(context, misListas[position].nombre, Toast.LENGTH_SHORT).show()
                val toActivity = Intent(context, ListActivity::class.java)
                toActivity.putExtra("indice", position)
                context.startActivity(toActivity)
            }*/
            holder.itemView.setOnLongClickListener {
                // TODO: abrir Diálogo con opciones de Renombrar y Eliminar la Lista seleccionada (y Cancelar)
                Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item, "list"),
                    Toast.LENGTH_SHORT
                ).show()
                listas.remove(misListas[position])
                notifyDataSetChanged()
                true
            }
            when {
                item.items.size < 1 && item.checks.size < 1 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_playlist_add_black_24dp)
                item.items.size < 1 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_done_all_green_24dp)
                item.items.size > 0 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_priority_high_red_24dp)
            }
            holder.bind(context, item, clickListener)
        }
        if (context is ListActivity) {
            val listaTotal = misListas[index].items + misListas[index].checks
            val item = listaTotal[position]
            when (item) {
                in misListas[index].checks -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                else -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            }
            holder.itemView.setOnLongClickListener {
                Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item, "item"),
                    Toast.LENGTH_SHORT
                ).show()
                if (item in lista.items) {
                    lista.items.remove(item)
                } else {
                    lista.checks.remove(item)
                }
                notifyDataSetChanged()
                true
            }
            holder.bind(context, misListas[index], clickListener)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val miView = view
        fun bind(context: Context, elemento: Lista, clickListener: (String) -> Unit) {
            if (context is MainActivity) {
                val listaNombre = miView.nombreLista as TextView
                listaNombre.text = elemento.nombre
                itemView.setOnClickListener { clickListener(elemento.nombre) }
            } else {
                val itemNombre = miView.nombreItem as TextView
                val listaTotal = elemento.items + elemento.checks
                val elementoNombre = listaTotal[adapterPosition] //listaTotal[position]
                itemNombre.text = elementoNombre
                itemView.setOnClickListener { clickListener(elementoNombre) }
            }
        }
    }
}