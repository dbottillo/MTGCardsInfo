package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView

class CardsPagerAdapter(private val context: Context,
                        private val showImage: Boolean,
                        private val cards: CardsCollection) : PagerAdapter() {

    override fun getCount(): Int {
        return cards.list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = MTGCardView(context)
        view.load(cards.list[position], showImage)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getPageTitle(position: Int): CharSequence {
        val card = cards.list[position]
        if (cards.isDeck) {
            return card.name + " (" + card.quantity + ")"
        }
        return card.name
    }

    fun getItem(currentItem: Int): MTGCard {
        return cards.list[currentItem]
    }

}
