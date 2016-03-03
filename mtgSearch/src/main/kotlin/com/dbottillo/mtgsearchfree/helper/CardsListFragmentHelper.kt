package com.dbottillo.mtgsearchfree.helper

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter
import com.dbottillo.mtgsearchfree.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.search.SearchParams
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import java.util.*

class CardsListFragmentHelper : OnCardListener {

    var gameSet: MTGSet
    var listView: ListView
    var emptyView: TextView
    var cards: ArrayList<MTGCard>
    var adapter: CardListAdapter
    var progressBar: SmoothProgressBar
    var searchParams: SearchParams? = null

    constructor(context: Context, params: SearchParams, rootView: View) {
        emptyView = rootView.findViewById(R.id.empty_view) as TextView
        listView = rootView.findViewById(R.id.card_list) as ListView
        progressBar = rootView.findViewById(R.id.progress) as SmoothProgressBar
        cards = ArrayList<MTGCard>()
        searchParams = params
        emptyView.setText(R.string.empty_search)
        gameSet = MTGSet(-1)
        gameSet.name = rootView.resources.getString(R.string.action_search)
        adapter = CardListAdapter(context, cards, searchParams != null, R.menu.card_option, this)
        listView.adapter = adapter
        //loadSearch()
    }

    constructor(context: Context, set: MTGSet, rootView: View) {
        emptyView = rootView.findViewById(R.id.empty_view) as TextView
        listView = rootView.findViewById(R.id.card_list) as ListView
        progressBar = rootView.findViewById(R.id.progress) as SmoothProgressBar
        cards = ArrayList<MTGCard>()
        gameSet = set
        emptyView.setText(R.string.empty_cards)

        adapter = CardListAdapter(context, cards, searchParams != null, R.menu.card_option, this)
        listView.adapter = adapter
    }

    override fun onCardSelected(card: MTGCard?, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun onOptionSelected(menuItem: MenuItem?, card: MTGCard?, position: Int) {
        throw UnsupportedOperationException()
    }
}

