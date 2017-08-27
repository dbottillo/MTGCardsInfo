package com.dbottillo.mtgsearchfree.ui.decks

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.cards.CardAdapterHelper
import com.dbottillo.mtgsearchfree.ui.cards.ListCardViewHolder
import com.dbottillo.mtgsearchfree.ui.cards.OnCardListener

class DeckCardAdapter(private val mContext: Context,
                      private val cards: MutableList<MTGCard>,
                      private val menuRes: Int,
                      private val onCardListener: OnCardListener) : RecyclerView.Adapter<ListCardViewHolder>() {

    fun add(card: MTGCard, position: Int) {
        val pos = if (position == -1) itemCount else position
        cards.add(pos, card)
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
        holder.itemView.setOnClickListener { onCardListener.onCardSelected(card, holder.adapterPosition) }
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}