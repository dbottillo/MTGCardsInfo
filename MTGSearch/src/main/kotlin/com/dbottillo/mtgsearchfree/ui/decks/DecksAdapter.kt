package com.dbottillo.mtgsearchfree.ui.lifecounter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.Deck

class DecksAdapter(val decks: List<Deck>,
                   val copy:(deck: Deck) -> Unit,
                   val delete:(deck: Deck) -> Unit,
                   val selected:(deck: Deck) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.decks_header, parent, false))
        }
        if (viewType == TYPE_FOOTER) {
            val footerView = LayoutInflater.from(parent.context).inflate(R.layout.decks_footer, parent, false)
            return FooterViewHolder(footerView)
        }
        return DeckViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_deck, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            holder as FooterViewHolder
            if (decks.isEmpty()){
                holder.emptyText.visibility = View.VISIBLE
            } else {
                holder.emptyText.visibility = View.GONE
            }
        }

        if (getItemViewType(position) == TYPE_DECK) {
            holder as DeckViewHolder
            val deck = decks[position-1]
            holder.name.text = deck.name
            holder.number.text = holder.row.context?.getString(R.string.deck_subtitle, deck.numberOfCards)
            holder.delete.setOnClickListener{ delete(deck) }
            holder.copy.setOnClickListener{ copy(deck) }
            holder.parent.setOnClickListener{ selected(deck) }
        }
    }

    override fun getItemCount(): Int {
        return decks.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TYPE_HEADER
            decks.size + 1 -> TYPE_FOOTER
            else -> TYPE_DECK
        }
    }

    class HeaderViewHolder(val row: View) : RecyclerView.ViewHolder(row)

    class FooterViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val emptyText : TextView = row.findViewById(R.id.empty_decks_text_view)
    }

    class DeckViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val parent: View = row.findViewById(R.id.deck_parent)
        val name: TextView = row.findViewById(R.id.deck_name)
        val number: TextView = row.findViewById(R.id.deck_number)
        val copy: ImageButton = row.findViewById(R.id.deck_copy)
        val delete: ImageButton = row.findViewById(R.id.delete_deck)
    }

}

const val TYPE_HEADER: Int = 0
const val TYPE_DECK: Int = 1
const val TYPE_FOOTER: Int = 2