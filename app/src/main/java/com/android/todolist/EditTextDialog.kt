package com.android.todolist

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_new_item.view.*
import kotlinx.android.synthetic.main.dialog_new_list.view.*


class EditTextDialog : DialogFragment() {

    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_HINT = "hint"
        private const val EXTRA_LAYOUT = "layout"

        fun newInstance(title: String? = null, hint: String? = null, layout: Int): EditTextDialog {
            val dialog = EditTextDialog()
            val args = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putString(EXTRA_HINT, hint)
                putInt(EXTRA_LAYOUT, layout)
            }
            dialog.arguments = args
            return dialog
        }
    }

    lateinit var editText: EditText
    var onOk: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(EXTRA_TITLE)
        val hint = arguments?.getString(EXTRA_HINT)
        val layout = arguments?.getInt(EXTRA_LAYOUT)
        val view = when (layout) {
            is Int -> activity!!.layoutInflater.inflate(layout, null)
            else -> null
        }
        if (view != null) when (layout) {
            R.layout.dialog_new_list -> editText = view.editText_newList
            R.layout.dialog_new_item -> editText = view.editText_newItem
        }
        editText.hint = hint

        val builder = AlertDialog.Builder(context!!)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (!editText.text.isBlank()) onOk?.invoke() else onCancel?.invoke()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                onCancel?.invoke()
            }

        return builder.create()
    }

}