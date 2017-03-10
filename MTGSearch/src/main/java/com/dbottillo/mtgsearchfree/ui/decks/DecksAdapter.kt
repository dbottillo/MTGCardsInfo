package com.dbottillo.mtgsearchfree.ui.lifecounter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.Deck

class DecksAdapter(val decks: List<Deck>, val listener: OnDecksListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val TYPE_HEADER: Int = 0
        private val TYPE_DECK: Int = 1
        private val TYPE_FOOTER: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == TYPE_HEADER) {
            return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.decks_header, parent, false))
        }
        if (viewType == TYPE_FOOTER) {
            val footerView = LayoutInflater.from(parent.context).inflate(R.layout.decks_footer, parent, false)
            footerView.findViewById(R.id.add_new_deck).setOnClickListener { listener.onAddDeck() }
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
        val emptyText : TextView = row.findViewById(R.id.empty_decks_text_view) as TextView
    }

    class DeckViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
/*
        val card: CardView = row.findViewById(R.id.life_counter_card) as CardView
        val name: TextView = row.findViewById(R.id.player_name) as TextView
        val life: TextView = row.findViewById(R.id.player_life) as TextView
        val poison: TextView = row.findViewById(R.id.player_poison) as TextView
        val poisonContainer: View = row.findViewById(R.id.life_counter_poison_container)
        val edit: ImageButton = row.findViewById(R.id.player_edit) as ImageButton
        val remove: ImageButton = row.findViewById(R.id.player_remove) as ImageButton
        val lifePlusOne: Button = row.findViewById(R.id.btn_life_plus_one) as Button
        val lifeMinusOne: Button = row.findViewById(R.id.btn_life_minus_one) as Button
        val lifePlusFive: Button = row.findViewById(R.id.btn_life_plus_five) as Button
        val lifeMinusFive: Button = row.findViewById(R.id.btn_life_minus_five) as Button
        val poisonPlusOne: Button = row.findViewById(R.id.btn_poison_plus_one) as Button
        val poisonMinusOne: Button = row.findViewById(R.id.btn_poison_minus_one) as Button*/
    }

}
