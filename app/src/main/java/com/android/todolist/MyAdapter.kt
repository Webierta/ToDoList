package com.android.todolist

import android.content.Context
import android.content.Intent
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
    private val index: Int  //,
    //private val clickListener: (String) -> Unit
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

    override fun getItemId(position: Int): Long {
        // here code for getting the right itemID,
        // i.e. return super.getItemId(mPosition);
        // where mPosition ist the Position in the Collection.
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {

        if (context is MainActivity) {
            val item = misListas[position]

            holder.itemView.setOnClickListener {
                val pos = misListas.indexOf(item)
                Toast.makeText(context, misListas[pos].nombre, Toast.LENGTH_SHORT).show()
                val toActivity = Intent(context, ListActivity::class.java)
                toActivity.putExtra("indice", pos)  // position
                context.startActivity(toActivity)
            }

            /*holder.itemView.setOnLongClickListener {
                // TODO: abrir Diálogo con opciones de Renombrar y Eliminar la Lista seleccionada (y Cancelar)
                Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item.nombre, "list"),
                    Toast.LENGTH_SHORT
                ).show()
                val pos = misListas.indexOf(item)  //val pos = listas.indexOf(item)
                misListas.remove(item) // remove(misListas[position])
                notifyItemRemoved(pos)
                true
            }*/

            when {
                item.items.size < 1 && item.checks.size < 1 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_playlist_add_black_24dp)
                item.items.size < 1 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_done_all_green_24dp)
                item.items.size > 0 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_priority_high_red_24dp)
            }
            holder.bind(context, item)  // holder.bind(context, item, clickListener)
        }

        if (context is ListActivity) {

            var listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
            val item = listaTotal[position]
            when (item) {
                in misListas[index].checks -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                else -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            }

            for ((index, cadaItem) in listaTotal.withIndex()){
                println("""
                    |$index $cadaItem""".trimMargin())
            }

            holder.itemView.setOnClickListener {
                if(item in misListas[index].items) {
                    println("ITEM A CHECK!!!!!!!!")
                    /*//FUNCIONA ?
                    val pos = misListas[index].items.indexOf(item)
                    val to = misListas[index].items.lastIndex
                    Toast.makeText(context, "$item de $pos a $to", Toast.LENGTH_SHORT).show()
                    misListas[index].items.removeAt(pos)
                    misListas[index].checks.add(item)
                    notifyItemMoved(pos, to)
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                    //it.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)*/
                    listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    //item = listaTotal[position]
                    val pos = listaTotal.indexOf(item)
                    val to = listaTotal.lastIndex
                    //listaTotal[pos+1] = listaTotal[pos].also { listaTotal[pos] = listaTotal[pos+1] }

                    for (i in pos until to) {
                        listaTotal[i] = listaTotal.set(i+1, listaTotal[i])
                        //misListas[i] = misListas.set(i+1, misListas[i])
                        //misListas[index].items[i] = misListas[index].items.set(i+1, misListas[index].items[i])
                    }

                    Toast.makeText(context, "$item de $pos a $to", Toast.LENGTH_SHORT).show()
                    misListas[index].items.remove(item)
                    misListas[index].checks.add(item)
                    notifyItemMoved(pos, to)
                    holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)

                    listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    for ((index, cadaItem) in listaTotal.withIndex()){
                        println("""
                    |$index $cadaItem""".trimMargin())
                    }

                } else {
                    println("CHECK A ITEM!!!!!!!!")
                    // notifyDataSetChanged()  // TODO: Soluciona errores pero quita movimiento

                    /*val pos = misListas[index].checks.indexOf(item)
                    val to = 0 // if (misListas[index].items.isEmpty()) 0 else misListas[index].items.lastIndex + 1
                    Toast.makeText(context, "$item de $pos a $to", Toast.LENGTH_SHORT).show()
                    misListas[index].checks.removeAt(pos)
                    misListas[index].items.add(item)
                    notifyItemMoved(pos, 0)*/

                    //notifyItemMoved(pos, to)
                    //val actual = holder.adapterPosition
                    listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    //item = listaTotal[position]
                    val pos = listaTotal.indexOf(item)
                    val to = 0
                    for (i in pos..to + 1) {
                        listaTotal[i] = listaTotal.set(i-1, listaTotal[i])
                    }
                    Toast.makeText(context, "$item de $pos a 0", Toast.LENGTH_SHORT).show()
                    misListas[index].checks.remove(item)
                    misListas[index].items.add(0, item)
                    //notifyItemChanged(pos, item)
                    //notifyItemChanged(0, item)
                    notifyItemMoved(pos, 0)

                    // TODO: UPDATE LISTAS
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
                    listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    for ((index, cadaItem) in listaTotal.withIndex()){
                        println("""
                    |$index $cadaItem""".trimMargin())
                    }
                }
            }

            holder.itemView.setOnLongClickListener {

                //val listaTotal = misListas[index].items + misListas[index].checks
                //val item = listaTotal[position]
                //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                //val item = listaTotal[position]
                //val posDelete = listaTotal.indexOf(item)

                /*Toast.makeText(
                    context,
                    context.getString(R.string.elemento_eliminado, item, "item"),
                    Toast.LENGTH_SHORT
                ).show()*/
                //val pos = misListas[index].items.indexOf(item)
                //val pos: Int
                listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                val pos = listaTotal.indexOf(item)
                Toast.makeText(context, context.getString(R.string.elemento_eliminado, item, "item"), Toast.LENGTH_SHORT).show()
                if (item in misListas[index].items) {  //  if (item in lista.items) {
                    //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    //pos = listaTotal.indexOf(item) // pos = misListas[index].items.indexOf(item)
                    //Toast.makeText(context, context.getString(R.string.elemento_eliminado, item, "item"), Toast.LENGTH_SHORT).show()
                    misListas[index].items.remove(item)  // lista.items.remove(item)
                    //notifyItemRemoved(pos)  // position
                    //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                } else {
                    //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                    //pos = listaTotal.indexOf(item)  // pos = misListas[index].checks.indexOf(item)
                    //Toast.makeText(context, context.getString(R.string.elemento_eliminado, item, "item"), Toast.LENGTH_SHORT).show()
                    misListas[index].checks.remove(item) // lista.checks.remove(item)
                    //notifyItemRemoved(pos)
                    //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                }
                notifyItemRemoved(pos)  // position
                listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>

                //notifyItemRemoved(pos)
                //listaTotal = (misListas[index].items + misListas[index].checks) as MutableList<String>
                for ((index, cadaItem) in listaTotal.withIndex()){
                    println("""
                    |$index $cadaItem""".trimMargin())
                }
                //notifyItemRemoved(pos)
                //notifyDataSetChanged()
                true
            }
            /*holder.itemView.setOnClickListener {
                if (item in misListas[index].items) {
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
            }*/

            //val listaTotal = misListas[index].items + misListas[index].checks
            //val item = listaTotal[position]

            /*when (item) {
                in misListas[index].checks -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_green_24dp)
                else -> holder.itemView.imgItem.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            }*/
            holder.bind(context, misListas[index])  // holder.bind(context, misListas[index], clickListener)
            //holder.update(item, misListas[index])
        }
    }

    /*private fun getItemPosition(item: Item): Int {
        var i = 0
        for (currentItem in misListas[index].checks) {
            if (currentItem.getItemId() === item.getItemId()) break
            i++
        }
        return i
    }*/

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
        /*fun update(item: String, elemento: Lista){
            val imgIcon = miView.imgItem as ImageView
            when (item) {
                in elemento.checks -> imgIcon.setImageResource(R.drawable.ic_check_box_green_24dp)
                else -> imgIcon.setImageResource(R.drawable.ic_check_box_outline_blank_gray_24dp)
            }
        }*/
    }
}