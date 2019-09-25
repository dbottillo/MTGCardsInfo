package com.dbottillo.mtgsearchfree.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.dbottillo.mtgsearchfree.core.R
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.Player
import java.lang.ref.WeakReference

class DialogUtil {

    private lateinit var refContext: WeakReference<Context>

    fun init(context: Context?) {
        this.refContext = WeakReference<Context>(context)
    }

    fun showEditPlayer(layoutId: Int, editTextId: Int, player: Player, listener: (newName: String) -> Unit) {
        if (refContext.get() == null) {
            return
        }
        val context = refContext.get()!!
        val alert = AlertDialog.Builder(context, R.style.MTGDialogTheme)

        alert.setTitle(context.getString(R.string.edit_player))

        val layoutInflater = LayoutInflater.from(context)
        /*@SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.dialog_edit_deck, null)
        val editText = view.findViewById(R.id.edit_text)*/
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(layoutId, null)
        val editText = view.findViewById<EditText>(editTextId)
        editText.setText(player.name)
        editText.setSelection(player.name.length)
        alert.setView(view)

        alert.setPositiveButton(context.getString(R.string.save)) { _, _ ->
            val value = editText.text.toString()
            listener(value)
        }

        alert.show()
    }

    fun showAddDeck(layoutId: Int, editTextId: Int, listener: (newName: String) -> Unit) {
        if (refContext.get() == null) {
            return
        }
        val context = refContext.get()!!
        val alert = AlertDialog.Builder(context, R.style.MTGDialogTheme)

        alert.setTitle(context.getString(R.string.new_deck_hint))

        val layoutInflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(layoutId, null)
        val editText = view.findViewById<EditText>(editTextId)
        alert.setView(view)

        alert.setPositiveButton(context.getString(R.string.add)) { _, _ ->
            val value = editText.text.toString()
            listener(value)
        }

        alert.show()
    }

    fun deleteDeck(deck: Deck, listener: (deck: Deck) -> Unit) {
        if (refContext.get() == null) {
            return
        }
        val context = refContext.get()!!
        AlertDialog.Builder(context, R.style.MTGDialogTheme)
                .setTitle(R.string.deck_delete_title)
                .setMessage(R.string.deck_delete_text)
                .setPositiveButton(R.string.deck_delete_confirmation) { _, _ ->
                    listener(deck)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }
}