package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
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
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.view.views.MTGCardView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CardsAdapter(val cards: List<MTGCard>,
                   val listener: OnCardListener,
                   val cardFilter: CardFilter?,
                   val configuration: CardAdapterConfiguration): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var colorFilterActive = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return HeaderViewHolder(inflater.inflate(R.layout.cards_header, parent, false))
        }

        val context = parent.context
        val columns = context.resources.getInteger(R.integer.cards_grid_column_count)
        val v = inflater.inflate(if (configuration.isGrid) R.layout.grid_item_card else R.layout.row_card, parent, false)
        if (configuration.isGrid) {
            val height = (parent.measuredWidth / columns.toDouble() * MTGCardView.RATIO_CARD).toInt()
            v.minimumHeight = height
        }
        return if (configuration.isGrid) GridCardViewHolder(v) else ListCardViewHolder(v)
    }

    override fun onBindViewHolder(originalHolder: RecyclerView.ViewHolder, position: Int) {
        if (position == ITEM_VIEW_TYPE_HEADER) {
            val holder = originalHolder as HeaderViewHolder
            holder.title.text = configuration.title
            if (configuration.headerIconTitle > -1) {
                //headerIconTitle = R.drawable.ic_edit_grey;
                var drawable = ContextCompat.getDrawable(holder.itemView.context, configuration.headerIconTitle)
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(holder.itemView.context, R.color.color_primary))
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                holder.title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
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
                holder.subTitle.text = spannableString as CharSequence?
            }
            holder.itemView.setOnClickListener { listener.onCardsHeaderSelected() }
            return
        }

        val holder = originalHolder as CardViewHolder
        val card = cards[position - 1]
        val context = holder.parent.context
        if (holder is GridCardViewHolder) {
            val gridCardViewHolder = holder
            gridCardViewHolder.loader.visibility = View.VISIBLE
            gridCardViewHolder.image.contentDescription = card.name
            Picasso.with(context.applicationContext).load(card.image)
                    .error(R.drawable.left_debug)
                    .into(gridCardViewHolder.image, object : Callback {
                        override fun onSuccess() {
                            gridCardViewHolder.loader.visibility = View.GONE
                        }

                        override fun onError() {
                            gridCardViewHolder.loader.visibility = View.GONE
                        }
                    })
        } else {
            val listCardViewHolder = holder as ListCardViewHolder
            CardAdapterHelper.bindView(context, card, listCardViewHolder, configuration.isSearch)
            CardAdapterHelper.setupMore(listCardViewHolder, context, card, position, configuration.menu, listener)
        }
        holder.parent.setOnClickListener { v ->
            listener.onCardSelected(card, holder.adapterPosition - 1, v.findViewById(R.id.grid_item_card_image))
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
        return cards.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: AppCompatTextView
        var subTitle: TextView
        var type: ImageButton
        var settings: ImageButton

        init {
            title = itemView.findViewById(R.id.title) as AppCompatTextView
            subTitle = itemView.findViewById(R.id.sub_title) as TextView
            type = itemView.findViewById(R.id.cards_view_type) as ImageButton
            settings = itemView.findViewById(R.id.cards_settings) as ImageButton
        }
    }

    companion object {

        private val ITEM_VIEW_TYPE_HEADER = 0
        private val ITEM_VIEW_TYPE_ITEM = 1
    }

}
