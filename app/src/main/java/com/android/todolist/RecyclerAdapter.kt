package com.android.todolist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*


class RecyclerAdapter(
    private val context: Context,
    private val misListas: MutableList<Lista> = ArrayList()
    //val listener: (Lista) -> Unit
    //val clickListener: (Lista) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.recyclerview_list_row, parent, false)
        //itemView.layoutParams.height = parent.measuredWidth / 8
        //val altura = parent.measuredHeight / 40
        //itemView.minimumHeight = altura
        return ViewHolder(itemView)
        //return ViewHolder(layoutInflater.inflate(R.layout.recyclerview_list_row, parent, false))
        //return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_list_row, parent, false))
    }

    override fun getItemCount(): Int {
        return misListas.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        val item = misListas[position]
        //holder.bind(item, context)
        //holder.listaNombre.text = misListas[position]
        holder.itemView.setOnClickListener {
            Toast.makeText(context, misListas[position].nombre, Toast.LENGTH_SHORT).show()
            val toActivity = Intent(context, ListActivity::class.java)
            //val posicion = listas.indexOf(lista)
            toActivity.putExtra("indice", position)
            //toActivity.putExtra("nombreLista", lista.nombre)
            context.startActivity(toActivity)
        }

        holder.itemView.setOnLongClickListener {
            Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
            listas.remove(misListas[position])
            notifyDataSetChanged()
            true
        }
        when {
            item.items.size < 1 && item.checks.size < 1 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_playlist_add_black_24dp)
            item.items.size < 1 && item.checks.size > 0 -> holder.itemView.imgLista.setImageResource(R.drawable.ic_done_all_green_24dp)
            item.items.size > 0 && item.checks.size > 0-> holder.itemView.imgLista.setImageResource(R.drawable.ic_priority_high_red_24dp)
        }
        holder.bind(item, context)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val listaNombre = view.nombreLista as TextView
        //private val listaIcono = view.imgLista as ImageView

        fun bind(lista: Lista, context: Context) {
            listaNombre.text = lista.nombre

            /*for ((i, estaLista) in listas.withIndex()) {
                if (estaLista.checks.size > 0 && estaLista.items.size < 1) {
                    itemView.imgLista.setImageResource(R.drawable.ic_done_all_green_24dp)
                    //listaIcono.setImageResource(R.drawable.ic_done_all_green_24dp)
                }
            }*/

            /*itemView.setOnClickListener {
                Toast.makeText(context, lista.nombre, Toast.LENGTH_SHORT).show()
                val toActivity = Intent(context, ListActivity::class.java)
                val posicion = listas.indexOf(lista)
                toActivity.putExtra("indice", posicion)
                //toActivity.putExtra("nombreLista", lista.nombre)
                context.startActivity(toActivity)
                //context.startActivity(Intent(context, ListActivity::class.java))
            }*/

            /*itemView.setOnLongClickListener {
                Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
                //MainActivity.removeList(position)
                // dialogo de confirmación
                // OK -> eliminar lista
                listas.remove(lista)  //listas.removeAt(position)
                // ACTUALIZAR ADAPTADOR
                context.startActivity(Intent(context, MainActivity::class.java))
                true
            }*/
            //itemView.setOnClickListener { clickListener(lista) }
        }
    }
}

