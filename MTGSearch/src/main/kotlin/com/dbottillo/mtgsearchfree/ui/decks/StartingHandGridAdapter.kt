package com.dbottillo.mtgsearchfree.ui.decks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.cards.GridCardViewHolder
import com.dbottillo.mtgsearchfree.ui.cards.ITEM_VIEW_TYPE_FOOTER
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class StartingHandGridAdapter(var cards: MutableList<StartingHandCard>,
                              val columns: Int,
                              val next: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        if (cards.isEmpty()) return 0
        return cards.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridCardViewHolder -> {
                val card = cards[position]
                holder.loader.visibility = View.VISIBLE
                holder.image.contentDescription = card.name
                card.image?.let {
                    Picasso.with(holder.itemView.context.applicationContext).load(card.image)
                            .error(R.drawable.left_debug)
                            .into(holder.image, object : Callback {
                                override fun onSuccess() {
                                    holder.loader.visibility = View.GONE
                                }

                                override fun onError() {
                                    holder.loader.visibility = View.GONE
                                }
                            })
                }
                holder.itemView.setOnClickListener(null)
            }
            is NextCardViewHolder -> {
                holder.itemView.setOnClickListener { next() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val height = (parent.measuredWidth / columns.toDouble() * MTGCardView.RATIO_CARD).toInt()

        when (viewType) {
            ITEM_VIEW_TYPE_FOOTER -> {
                val v = inflater.inflate(R.layout.grid_footer, parent, false)
                v.minimumHeight = height
                return GridFooterViewHolder(v)
            }
            ITEM_VIEW_TYPE_NEXT_CARD -> {
                val v = inflater.inflate(R.layout.starting_hand_next, parent, false)
                v.minimumHeight = height
                return NextCardViewHolder(v)
            }
            else -> {
                val v = inflater.inflate(R.layout.grid_item_card, parent, false)
                v.minimumHeight = height
                return GridCardViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            cards.size -> ITEM_VIEW_TYPE_NEXT_CARD
            cards.size + 1 -> ITEM_VIEW_TYPE_FOOTER
            else -> ITEM_VIEW_TYPE_CARD
        }
    }

    fun add(mtgCard: StartingHandCard) {
        cards.add(mtgCard)
        notifyDataSetChanged()
    }
}

const val ITEM_VIEW_TYPE_CARD = 0
const val ITEM_VIEW_TYPE_NEXT_CARD = 1

class NextCardViewHolder(view: View) : RecyclerView.ViewHolder(view)

class GridFooterViewHolder(view: View) : RecyclerView.ViewHolder(view)