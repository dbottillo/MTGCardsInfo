package com.dbottillo.mtgsearchfree.view.fragments;

import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter;
import com.dbottillo.mtgsearchfree.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.communication.DataManager;
import com.dbottillo.mtgsearchfree.communication.events.CardsEvent;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.dbottillo.mtgsearchfree.search.SearchParams;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MTGSetFragment extends BasicFragment implements View.OnClickListener, OnCardListener {

    private MTGSet gameSet;
    private ListView listView;
    private TextView emptyView;
    private ArrayList<MTGCard> cards;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private SearchParams searchParams;

    protected void setupSetFragment(View rootView, SearchParams searchParams) {
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        if (searchParams != null) {
            emptyView.setText(R.string.empty_search);
            this.searchParams = searchParams;
        } else {
            emptyView.setText(R.string.empty_cards);
        }

        if (searchParams != null) {
            gameSet = new MTGSet(-1);
            gameSet.setName(getString(R.string.action_search));
            loadSearch();
        }

        listView = (ListView) rootView.findViewById(R.id.card_list);

        cards = new ArrayList<>();
        adapter = new CardListAdapter(getActivity(), cards, searchParams != null, R.menu.card_option, this);
        listView.setAdapter(adapter);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

    }

    public String getPageTrack() {
        if (searchParams != null) {
            return "/search";
        }
        if (gameSet != null) {
            return "/set/" + gameSet.getCode();
        }
        return null;
    }

    protected void loadSet(MTGSet set) {
        this.gameSet = set;
        DataManager.execute(DataManager.TASK.SET_CARDS, gameSet);
    }

    protected void loadSearch() {
        DataManager.execute(DataManager.TASK.SEARCH_CARDS, searchParams);
    }

    public void onEventMainThread(CardsEvent event) {
        /*LOG.e("on event main thread cards");
        if (event.isError) {
        Toast.makeText(activity, event.errorMessage, Toast.LENGTH_SHORT).show()
        TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "card-main", event.errorMessage)
        } else {
            *//* gameSet!!.clear()
             for (card in event.result) {
                 gameSet!!.addCard(card)
             }*//*
        //            filterPresenter.loadFilter();
        }
        bus.removeStickyEvent(event)*/
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

    public void updateSetFragment() {
        //filterPresenter.loadFilter();
    }

    @Override
    public void onCardSelected(MTGCard card, int position) {

    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {

    }

    @Override
    public void onClick(View v) {

    }

        /*public fun onClick( View v) {
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
        MTGApp.cardsToDisplay= cards;
        *//*cardsView.putExtra(MTGCardsFragment.POSITION, position)
        cardsView.putExtra(MTGCardsFragment.TITLE, gameSet!!.name)*//*
        startActivity(cardsView)
        }

        override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        if (menuItem.itemId == R.id.action_add_to_deck) {
        DialogHelper.open(dbActivity!!, "add_to_deck", AddToDeckFragment.newInstance(card))
        } else {
        DataManager.execute(DataManager.TASK.SAVE_CARD, card)
        }
        }*/
}
