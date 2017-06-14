package com.dbottillo.mtgsearchfree.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.cards.CardAdapterHelper
import com.dbottillo.mtgsearchfree.ui.cards.CardViewHolder
import com.dbottillo.mtgsearchfree.ui.cards.ListCardViewHolder

import java.util.ArrayList

class DeckCardAdapter(private val mContext: Context,
                      private val cards: MutableList<MTGCard>,
                      private val menuRes: Int,
                      private val onCardListener: OnCardListener) : RecyclerView.Adapter<ListCardViewHolder>() {

    fun add(card: MTGCard, position: Int) {
        var position = position
        position = if (position == -1) itemCount else position
        cards.add(position, card)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        if (position < itemCount) {
            cards.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCardViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.row_card, parent, false)
        return ListCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListCardViewHolder, position: Int) {
        val card = cards[position]
        CardAdapterHelper.bindView(mContext, card, holder, false, true)
        CardAdapterHelper.setupMore(holder, mContext, card, position, menuRes, onCardListener)
        holder.itemView.setOnClickListener { v -> onCardListener.onCardSelected(card, holder.adapterPosition, v) }
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}