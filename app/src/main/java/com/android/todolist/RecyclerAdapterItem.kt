package com.android.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class RecyclerAdapterItem(
    private val context: Context,
    private val misListas: MutableList<Lista> = ArrayList(),
    private val index: Int,
    private val clickListener: (String) -> Unit
    //private val longClickListener: (String) -> Boolean
    //private val todosItems: MutableList<Item> = ArrayList()
) : RecyclerView.Adapter<RecyclerAdapterItem.ViewHolder>() {

    //private val miLista = misListas[index]
    //private val listaTotal = miLista.items + miLista.checks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterItem.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.recyclerview_item_row, parent, false)
        //itemView.layoutParams.height = parent.measuredWidth / 8
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        //listaTotal = lista.items + lista.checks
        return misListas[index].items.size + misListas[index].checks.size
        //return listaTotal.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapterItem.ViewHolder, position: Int) {
        val listaTotal = misListas[index].items + misListas[index].checks
        val item = listaTotal[position]
        when (item) {
            in misListas[index].checks -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
            else -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
        }
        holder.itemView.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.removed_item, item), Toast.LENGTH_SHORT).show()
            if (item in lista.items) {
                lista.items.remove(item)
            } else {
                lista.checks.remove(item)
            }
            notifyDataSetChanged()
            true
        }
        holder.bind(item, clickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemNombre = view.nombreItem as TextView
        //private val itemIcon = view.imgItem as ImageView

        //fun bind(item: String, context: Context) {
        fun bind(item: String, clickListener: (String) -> Unit) {
            //var todosItems = lista.items + lista.checks
            itemNombre.text = item
            /*itemView.setOnClickListener {
                if (item in lista.items ) {
                    lista.items.remove(item)
                    lista.checks.add(item)
                } else {
                    lista.checks.remove(item)
                    lista.items.add(item)
                }
            }*/
            itemView.setOnClickListener { clickListener(item) }
        }
    }

}