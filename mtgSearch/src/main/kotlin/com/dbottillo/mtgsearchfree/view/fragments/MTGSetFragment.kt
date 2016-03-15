package com.dbottillo.mtgsearchfree.view.fragments

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter
import com.dbottillo.mtgsearchfree.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.communication.DataManager
import com.dbottillo.mtgsearchfree.communication.events.CardsEvent
import com.dbottillo.mtgsearchfree.dialog.AddToDeckFragment
import com.dbottillo.mtgsearchfree.helper.DialogHelper
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.search.SearchParams
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import java.util.*

abstract class MTGSetFragment : BasicFragment(), View.OnClickListener, OnCardListener {

    private var gameSet: MTGSet? = null
    private var listView: ListView? = null
    private var emptyView: TextView? = null
    private var cards: ArrayList<MTGCard>? = null
    private var adapter: CardListAdapter? = null
    private var progressBar: SmoothProgressBar? = null
    private var searchParams: SearchParams? = null

    @JvmOverloads protected fun setupSetFragment(rootView: View, searchParams: SearchParams? = null) {
        emptyView = rootView.findViewById(R.id.empty_view) as TextView
        if (searchParams != null) {
            emptyView!!.setText(R.string.empty_search)
            this.searchParams = searchParams
        } else {
            emptyView!!.setText(R.string.empty_cards)
        }

        if (searchParams != null) {
            gameSet = MTGSet(-1)
            gameSet!!.name = getString(R.string.action_search)
            loadSearch()
        }

        listView = rootView.findViewById(R.id.card_list) as ListView

        cards = ArrayList<MTGCard>()
        adapter = CardListAdapter(activity, cards, searchParams != null, R.menu.card_option, this)
        listView!!.adapter = adapter

        progressBar = rootView.findViewById(R.id.progress) as SmoothProgressBar

    }

    override fun getPageTrack(): String? {
        if (isASearch) {
            return "/search"
        }
        if (gameSet != null) {
            return "/set/" + gameSet!!.code
        }
        return null;
    }

    protected fun loadSet(set: MTGSet) {
        this.gameSet = set
        DataManager.execute(DataManager.TASK.SET_CARDS, gameSet)
    }

    protected fun loadSearch() {
        DataManager.execute(DataManager.TASK.SEARCH_CARDS, searchParams)
    }

    fun onEventMainThread(event: CardsEvent) {
        LOG.e("on event main thread cards")
        if (event.isError) {
            Toast.makeText(activity, event.errorMessage, Toast.LENGTH_SHORT).show()
            TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "card-main", event.errorMessage)
        } else {
            /* gameSet!!.clear()
             for (card in event.result) {
                 gameSet!!.addCard(card)
             }*/
            //            filterPresenter.loadFilter();
        }
        bus.removeStickyEvent(event)
    }

    /*fun updateContent(filter: CardFilter) {
        LOG.e("update content")
        cards!!.clear()
        CardsHelper.filterCards(filter, searchParams, gameSet!!.cards, cards)
        val wubrgSort = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true)
        CardsHelper.sortCards(wubrgSort, cards)

        adapter!!.notifyDataSetChanged()
        emptyView!!.visibility = if (adapter!!.count == 0) View.VISIBLE else View.GONE
        listView!!.smoothScrollToPosition(0)

        if (gameSet!!.cards.size == CardDataSource.LIMIT) {
            val footer = LayoutInflater.from(activity).inflate(R.layout.search_bottom, listView, false)
            val moreResult = footer.findViewById(R.id.more_result) as TextView
            moreResult.text = resources.getQuantityString(R.plurals.search_limit, CardDataSource.LIMIT, CardDataSource.LIMIT)
            listView!!.addFooterView(footer)
        }
        progressBar!!.visibility = View.GONE

        emptyView!!.visibility = if (adapter!!.count == 0) View.VISIBLE else View.GONE
    }*/

    fun updateSetFragment() {
        //filterPresenter.loadFilter();
    }

    override fun onClick(v: View) {
        if (v.id == R.id.open_play_store) {
            openPlayStore()
        }
    }

    private val isASearch: Boolean
        get() = searchParams != null

    override fun onCardSelected(card: MTGCard, position: Int) {
        if (isASearch && listView!!.footerViewsCount == 1 && position == cards!!.size) {
            return
        }
        if (isASearch) {
            TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, "search pos:" + position)
        } else {
            TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet!!.name + " pos:" + position)
        }
        val cardsView = Intent(activity, CardsActivity::class.java)
        MTGApp.cardsToDisplay = cards
        /*cardsView.putExtra(MTGCardsFragment.POSITION, position)
        cardsView.putExtra(MTGCardsFragment.TITLE, gameSet!!.name)*/
        startActivity(cardsView)
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        if (menuItem.itemId == R.id.action_add_to_deck) {
            DialogHelper.open(dbActivity!!, "add_to_deck", AddToDeckFragment.newInstance(card))
        } else {
            DataManager.execute(DataManager.TASK.SAVE_CARD, card)
        }
    }
}
