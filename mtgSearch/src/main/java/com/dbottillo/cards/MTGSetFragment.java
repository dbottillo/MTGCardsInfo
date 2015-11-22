package com.dbottillo.cards;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.CardListAdapter;
import com.dbottillo.adapters.OnCardListener;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.CardsEvent;
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.dialog.AddToDeckFragment;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.search.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public abstract class MTGSetFragment extends DBFragment implements View.OnClickListener, OnCardListener {

    private MTGSet gameSet;
    private ListView listView;
    private TextView emptyView;
    private ArrayList<MTGCard> cards;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private SearchParams searchParams;

    protected void setupSetFragment(View rootView) {
        setupSetFragment(rootView, null);
    }

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

    @Override
    public String getPageTrack() {
        if (isASearch()) {
            return "/search";
        }
        return "/set/" + gameSet.getCode();
    }

    protected void loadSet(MTGSet set) {
        this.gameSet = set;
        DataManager.execute(DataManager.TASK.SET_CARDS, gameSet.getId() + "");
    }

    protected void loadSearch() {
        DataManager.execute(DataManager.TASK.SEARCH_CARDS, searchParams);
    }

    public void onEventMainThread(CardsEvent event) {
        if (event.isError()) {
            Toast.makeText(getActivity(), event.getErrorMessage(), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "card-main", event.getErrorMessage());
        } else {
            gameSet.clear();
            for (MTGCard card : event.getResult()) {
                gameSet.addCard(card);
            }
            populateCardsWithFilter();
            if (gameSet.getCards().size() == CardsDatabaseHelper.LIMIT) {
                View footer = LayoutInflater.from(getActivity()).inflate(R.layout.search_bottom, listView, false);
                TextView moreResult = (TextView) footer.findViewById(R.id.more_result);
                moreResult.setText(getResources().getQuantityString(R.plurals.search_limit, CardsDatabaseHelper.LIMIT, CardsDatabaseHelper.LIMIT));
                listView.addFooterView(footer);
            }
            progressBar.setVisibility(View.GONE);

            emptyView.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
        }
        bus.removeStickyEvent(event);
    }

    private void populateCardsWithFilter() {
        cards.clear();
        SharedPreferences sharedPreferences = getSharedPreferences();
        for (MTGCard card : gameSet.getCards()) {
            boolean toAdd = false;
            if (searchParams ==  null) {
                if (card.isWhite() && sharedPreferences.getBoolean(FilterHelper.FILTER_WHITE, true)) {
                    toAdd = true;
                }
                if (card.isBlue() && sharedPreferences.getBoolean(FilterHelper.FILTER_BLUE, true)) {
                    toAdd = true;
                }
                if (card.isBlack() && sharedPreferences.getBoolean(FilterHelper.FILTER_BLACK, true)) {
                    toAdd = true;
                }
                if (card.isRed() && sharedPreferences.getBoolean(FilterHelper.FILTER_RED, true)) {
                    toAdd = true;
                }
                if (card.isGreen() && sharedPreferences.getBoolean(FilterHelper.FILTER_GREEN, true)) {
                    toAdd = true;
                }
                if (card.isLand() && sharedPreferences.getBoolean(FilterHelper.FILTER_LAND, true)) {
                    toAdd = true;
                }
                if (card.isArtifact() && sharedPreferences.getBoolean(FilterHelper.FILTER_ARTIFACT, true)) {
                    toAdd = true;
                }
                if (card.isEldrazi() && sharedPreferences.getBoolean(FilterHelper.FILTER_ELDRAZI, true)) {
                    toAdd = true;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_COMMON, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_UNCOMMON, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_RARE, true)) {
                    toAdd = false;
                }
                if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)
                        && !sharedPreferences.getBoolean(FilterHelper.FILTER_MYHTIC, true)) {
                    toAdd = false;
                }
            } else {
                // for search, filter don't apply
                toAdd = true;
            }

            if (toAdd) {
                cards.add(card);
            }

            boolean wubrgSort = getSharedPreferences().getBoolean(PREF_SORT_WUBRG, true);
            if (wubrgSort) {
                Collections.sort(cards, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.compareTo(card2);
                    }
                });
            } else {
                Collections.sort(cards, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.getName().compareTo(card2.getName());
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
        listView.smoothScrollToPosition(0);
    }

    public void updateSetFragment() {
        populateCardsWithFilter();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_play_store) {
            openPlayStore();
        }
    }

    private boolean isASearch() {
        return searchParams != null;
    }

    @Override
    public void onCardSelected(MTGCard card, int position) {
        if (isASearch() && listView.getFooterViewsCount() == 1 && position == cards.size()) {
            return;
        }
        if (isASearch()) {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, "search pos:" + position);
        } else {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet.getName() + " pos:" + position);
        }
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        MTGApp.setCardsToDisplay(cards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, gameSet.getName());
        startActivity(cardsView);
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        getDBActivity().openDialog("add_to_deck", AddToDeckFragment.newInstance(card));
    }
}
