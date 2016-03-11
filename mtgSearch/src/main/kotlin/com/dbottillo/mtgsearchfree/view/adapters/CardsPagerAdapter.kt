package com.dbottillo.mtgsearchfree.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.view.views.MTGCardView
import java.util.*

class CardsPagerAdapter(var context: Context, var deck: Boolean, var cards: ArrayList<MTGCard>) : PagerAdapter() {

    override fun getCount(): Int {
        return cards.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        var view = MTGCardView(context)
        view.load(cards.get(position), true)
        collection.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence {
        val card = cards[position]
        if (deck) {
            return card.name + " (" + card.quantity + ")"
        }
        return card.name
    }
}
