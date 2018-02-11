package com.dbottillo.mtgsearchfree.ui.cards

import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.dbottillo.mtgsearchfree.ui.views.RATIO_CARD
import com.dbottillo.mtgsearchfree.util.loadInto

class CardsAdapter(var cards: List<MTGCard>,
                   val listener: OnCardListener,
                   val cardFilter: CardFilter?,
                   val configuration: CardAdapterConfiguration) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var colorFilterActive = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return HeaderViewHolder(inflater.inflate(R.layout.cards_header, parent, false))
        }

        if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return FooterViewHolder(inflater.inflate(R.layout.cards_footer, parent, false))
        }

        val context = parent.context
        val columns = context.resources.getInteger(R.integer.cards_grid_column_count)
        val v = inflater.inflate(if (configuration.isGrid) R.layout.grid_item_card else R.layout.row_card, parent, false)
        if (configuration.isGrid) {
            val height = (parent.measuredWidth / columns.toDouble() * RATIO_CARD).toInt()
            v.minimumHeight = height
        }
        return if (configuration.isGrid) GridCardViewHolder(v) else ListCardViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.title.text = configuration.title
            holder.type.setOnClickListener { listener.onCardsViewTypeSelected() }
            holder.settings.setOnClickListener { listener.onCardsSettingSelected() }
            if (configuration.isGrid) {
                holder.type.setImageResource(R.drawable.cards_grid_type)
            } else {
                holder.type.setImageResource(R.drawable.cards_list_type)
            }
            if (cardFilter == null) {
                holder.subTitle.visibility = View.GONE
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            } else {
                holder.subTitle.visibility = View.VISIBLE
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                val spannableString = SpannableString("WUBRG - ALE - CURM")
                if (colorFilterActive == -1) {
                    colorFilterActive = ContextCompat.getColor(holder.itemView.context, R.color.color_accent)
                }
                checkSpannable(spannableString, cardFilter.white, 0)
                checkSpannable(spannableString, cardFilter.blue, 1)
                checkSpannable(spannableString, cardFilter.black, 2)
                checkSpannable(spannableString, cardFilter.red, 3)
                checkSpannable(spannableString, cardFilter.green, 4)

                checkSpannable(spannableString, cardFilter.artifact, 8)
                checkSpannable(spannableString, cardFilter.land, 9)
                checkSpannable(spannableString, cardFilter.eldrazi, 10)

                checkSpannable(spannableString, cardFilter.common, 14)
                checkSpannable(spannableString, cardFilter.uncommon, 15)
                checkSpannable(spannableString, cardFilter.rare, 16)
                checkSpannable(spannableString, cardFilter.mythic, 17)
                holder.subTitle.text = spannableString
            }
            holder.title.setOnClickListener { listener.onTitleHeaderSelected() }
            holder.subTitle.setOnClickListener { listener.onCardsSettingSelected() }
            return
        }

        if (holder is CardViewHolder) {
            val card = cards[position - 1]
            val context = holder.parent.context
            if (holder is GridCardViewHolder) {
                card.loadInto(holder.loader, holder.image)
            } else {
                val listCardViewHolder = holder as ListCardViewHolder
                CardAdapterHelper.bindView(context, card, listCardViewHolder, configuration.isSearch)
                CardAdapterHelper.setupMore(listCardViewHolder, context, card, position, configuration.menu, listener)
            }
            holder.parent.setOnClickListener {
                listener.onCardSelected(card, holder.adapterPosition - 1)
            }
        }
    }

    private fun checkSpannable(spannableString: SpannableString, on: Boolean, start: Int) {
        if (on) {
            spannableString.setSpan(ForegroundColorSpan(colorFilterActive), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return cards.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_VIEW_TYPE_HEADER
            cards.size + 1 -> ITEM_VIEW_TYPE_FOOTER
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: AppCompatTextView = itemView.findViewById(R.id.title)
        var subTitle: TextView = itemView.findViewById(R.id.sub_title)
        var type: ImageButton = itemView.findViewById(R.id.cards_view_type)
        var settings: ImageButton = itemView.findViewById(R.id.cards_settings)
    }

    internal inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}


const val ITEM_VIEW_TYPE_HEADER = 0
const val ITEM_VIEW_TYPE_ITEM = 1
const val ITEM_VIEW_TYPE_FOOTER = 2

