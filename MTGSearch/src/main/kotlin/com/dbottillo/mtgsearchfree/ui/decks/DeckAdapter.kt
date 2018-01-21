package com.dbottillo.mtgsearchfree.ui.decks

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.gone

class DeckAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var sectionsMap: MutableMap<Int, String> = mutableMapOf()
    private var cardsMap: MutableMap<Int, MTGCard> = mutableMapOf()

    var cardListener: OnDeckCardListener? = null

    fun setSections(sections: List<DeckSection>) {
        sectionsMap.clear()
        cardsMap.clear()
        var pos = 0
        sections.forEach { section ->
            if (section.cards.isNotEmpty()) {
                sectionsMap.put(pos, section.title)
                pos++
                section.cards.forEach { card ->
                    cardsMap.put(pos, card)
                    pos++
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getCards(): List<MTGCard> {
        return cardsMap.map { it.value }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is DeckHeaderViewHolder) {
            holder.bind(sectionsMap.getValue(position))
            return
        }
        (holder as DeckCardViewHolder).bind(cardsMap.getValue(position), cardListener)
        /*holder.row.setOnClickListener {
            onSectionClicked(articlesMap.getValue(position))
        }*/
    }

    override fun getItemCount(): Int = sectionsMap.size + cardsMap.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> DeckHeaderViewHolder(inflater.inflate(R.layout.deck_card_section, parent, false))
            else -> DeckCardViewHolder(inflater.inflate(R.layout.row_card, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (sectionsMap.containsKey(position)) {
            return TYPE_HEADER
        }
        return TYPE_CARD
    }

}

const val TYPE_HEADER: Int = 0
const val TYPE_CARD: Int = 1

class DeckSection(val title: String, val cards: List<MTGCard>)

class DeckHeaderViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
    val title: TextView by lazy { row.findViewById<TextView>(R.id.text_section) }

    fun bind(sectionTitle: String) {
        title.text = sectionTitle
    }
}

class DeckCardViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
    var name: TextView = row.findViewById(R.id.card_name)
    var setName: TextView = row.findViewById(R.id.card_set_name)
    var rarity: TextView = row.findViewById(R.id.card_rarity)
    var cost: TextView = row.findViewById(R.id.card_cost)
    var indicator: View = row.findViewById(R.id.card_indicator)
    var more: ImageButton = row.findViewById(R.id.card_more_option)

    fun bind(card: MTGCard, listener: OnDeckCardListener?) {
        val resources = row.context.resources
        name.text = resources.getString(R.string.row_card_name, card.quantity.toString() + " ", card.name)
        rarity.setTextColor(when (card.rarity.toLowerCase()) {
            CardFilter.FILTER_COMMON -> R.color.common
            CardFilter.FILTER_UNCOMMON -> R.color.uncommon
            CardFilter.FILTER_RARE -> R.color.rare
            else -> R.color.mythic
        })
        rarity.text = if (card.rarity.isNotEmpty()) card.rarity else ""
        if (card.manaCost.isNotEmpty()) {
            cost.text = card.manaCost.replace("{", "").replace("}", "")
            cost.setTextColor(card.getMtgColor(row.context))
        } else {
            cost.text = "-"
        }
        setName.gone()
        (indicator.background as GradientDrawable).setColor(card.getMtgColor(row.context))
        row.setOnClickListener { listener?.onCardSelected(card) }
        more.setOnClickListener {
            val popupMenu = PopupMenu(row.context, more)
            val menu = popupMenu.menu

            popupMenu.menuInflater.inflate(R.menu.deck_card, menu)
            val moveOne = menu.getItem(3)
            moveOne.setTitle(if (card.isSideboard) R.string.move_card_to_deck else R.string.move_card_to_sideboard)
            val moveAll = menu.getItem(4)
            moveAll.setTitle(if (card.isSideboard) R.string.move_all_card_to_deck else R.string.move_all_card_to_sideboard)
            popupMenu.setOnMenuItemClickListener { item ->
                listener?.onOptionSelected(item, card)
                false
            }
            popupMenu.show()
        }
    }
}

interface OnDeckCardListener {
    fun onCardSelected(card: MTGCard)
    fun onOptionSelected(menuItem: MenuItem, card: MTGCard)
}