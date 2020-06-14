package com.dbottillo.mtgsearchfree.cards

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.views.CardPresenter
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.dbottillo.mtgsearchfree.util.TrackingManager

class CardsPagerAdapter(
    private val context: Context,
    private val showImage: Boolean,
    private val cards: CardsCollection,
    private val cardPresenter: CardPresenter,
    private val trackingManager: TrackingManager
) : PagerAdapter() {

    override fun getCount(): Int {
        return cards.list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = MTGCardView(context)
        view.init(cardPresenter, trackingManager)
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
