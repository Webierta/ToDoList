package com.android.todolist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import java.util.*
import kotlin.math.roundToInt


class DragManageAdapter(adapter: MyAdapter, context: Context, dragDirs: Int, swipeDirs: Int) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var nameAdapter = adapter
    private var contexto = context
    private var tareasViewModel = run {
        ViewModelProviders.of(contexto as AppCompatActivity).get(TaskViewModel::class.java)
    }
    private val deleteIcon = contexto.resources.getDrawable(R.drawable.ic_delete_white_24dp, null)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (contexto is MainActivity) {
            if (from < to) for (i in from until to) Collections.swap(listas, i, i + 1)
            // for (i in from until to) misListas[i] = misListas.set(i + 1, misListas[i])
            else for (i in from downTo to + 1) Collections.swap(listas, i, i - 1)
            // for (i in from..to + 1) misListas[i] = misListas.set(i - 1, misListas[i])
            nameAdapter.notifyItemMoved(from, to)
            tareasViewModel.clearTabla()
            for (ls in listas) tareasViewModel.saveTarea(
                TaskEntity(ls.nombre, ls.items.joinToString(), ls.checks.joinToString())
            )
        }

        if (contexto is ListActivity) {
            val indice = nameAdapter.index
            if (from < listas[indice].items.size && to < listas[indice].items.size) {
                if (from < to) for (i in from until to) Collections.swap(listas[indice].items, i, i + 1)
                else for (i in from downTo to + 1) Collections.swap(listas[indice].items, i, i - 1)
                nameAdapter.notifyItemMoved(from, to)
                tareasViewModel.updateTarea(
                    TaskEntity(listas[indice].nombre, listas[indice].items.joinToString(), listas[indice].checks.joinToString())
                )
            } else {
                return false
            }
        }

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        /*if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
            viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#deeaca"))  // #D3D3D3
        }*/
        val controlSelected = when {
            contexto is MainActivity -> true
            viewHolder != null && contexto is ListActivity && viewHolder.adapterPosition < listas[nameAdapter.index].items.size -> true
            else -> false
        }
        if (controlSelected && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
            viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#deeaca"))  // #D3D3D3
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)  // 0x00000000
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT && contexto is MainActivity) {
            val pos = viewHolder.adapterPosition
            val deletedLista = listas[pos]
            tareasViewModel.deleteTarea(
                TaskEntity(listas[pos].nombre, listas[pos].items.joinToString(), listas[pos].checks.joinToString())
            )
            listas.removeAt(pos)
            nameAdapter.notifyItemRemoved(pos)
            Snackbar
                .make(
                    viewHolder.itemView,
                    contexto.getString(R.string.list_delete, deletedLista.nombre),
                    Snackbar.LENGTH_LONG
                )
                .setAction(R.string.undo) { undoDelete(pos, deletedLista) }
                .show()
        }
        if (direction == ItemTouchHelper.RIGHT && contexto is ListActivity) {
            val itemValue = (viewHolder.itemView.nombreItem as TextView).text.toString()
            // (rv_items.findViewHolderForAdapterPosition(viewHolder.adapterPosition)?.itemView?.nombreItem as TextView).text.toString()
            val listaTotal = nameAdapter.getListaItems()
            val pos = listaTotal.indexOf(itemValue)
            val indice = nameAdapter.index
            if (itemValue in listas[indice].items) listas[indice].items.remove(itemValue)
            else listas[indice].checks.remove(itemValue)
            nameAdapter.notifyItemRemoved(pos)
            Snackbar
                .make(
                    viewHolder.itemView,
                    contexto.getString(R.string.item_delete, itemValue),
                    Snackbar.LENGTH_SHORT
                )
                .show()
            tareasViewModel.updateTarea(
                TaskEntity(listas[indice].nombre, listas[indice].items.joinToString(), listas[indice].checks.joinToString())
            )
        }
    }

    private fun undoDelete(pos: Int, listaDeleted: Lista) {
        listas.add(pos, listaDeleted)
        nameAdapter.notifyItemInserted(pos)
        tareasViewModel.saveTarea(
            TaskEntity(listaDeleted.nombre, listaDeleted.items.joinToString(), listaDeleted.checks.joinToString())
        )
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        c.clipRect(0f, viewHolder.itemView.top.toFloat(), dX, viewHolder.itemView.bottom.toFloat())
        if (dX < viewHolder.itemView.width / 3) c.drawColor(Color.GRAY) else c.drawColor(Color.RED)
        val iconMargin = contexto.resources.getDimension(R.dimen.fab_margin).roundToInt()
        deleteIcon.bounds = Rect(
            iconMargin,
            viewHolder.itemView.top + iconMargin,
            iconMargin + deleteIcon.intrinsicWidth,
            viewHolder.itemView.top + deleteIcon.intrinsicHeight + iconMargin
        )
        deleteIcon.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}