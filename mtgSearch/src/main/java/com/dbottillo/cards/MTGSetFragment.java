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
import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.dialog.AddToDeckFragment;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public abstract class MTGSetFragment extends DBFragment implements View.OnClickListener, OnCardListener {

    private static DBAsyncTask currentTask = null;
    boolean isASearch = false;
    private MTGSet gameSet;
    private ListView listView;
    private TextView emptyView;
    private ArrayList<MTGCard> cards;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private String query;

    protected void setupSetFragment(View rootView, boolean isASearch) {
        setupSetFragment(rootView, isASearch, null);
    }

    protected void setupSetFragment(View rootView, boolean isASearch, String query) {
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        if (isASearch) {
            emptyView.setText(R.string.empty_search);
        } else {
            emptyView.setText(R.string.empty_cards);
        }

        this.isASearch = isASearch;

        if (isASearch) {
            this.query = query;
            gameSet = new MTGSet(-1);
            gameSet.setName(query);
            loadSearch();
        }

        listView = (ListView) rootView.findViewById(R.id.card_list);

        if (isASearch) {
            View header = LayoutInflater.from(getActivity()).inflate(R.layout.search_header, null);
            TextView searchQueryText = (TextView) header.findViewById(R.id.search_query);
            searchQueryText.setText(query);
            listView.addHeaderView(header);
        }

        cards = new ArrayList<>();
        adapter = new CardListAdapter(getActivity(), cards, isASearch, R.menu.card_option, this);
        listView.setAdapter(adapter);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentTask != null) {
            currentTask.attach(getActivity(), taskListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (currentTask != null) {
            currentTask.detach();
        }
    }

    @Override
    public String getPageTrack() {
        if (isASearch) return "/search";
        return "/set/" + gameSet.getCode();
    }
/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        if (isASearch) {
            currentTask = new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_SEARCH);
            currentTask.execute(query);
        } else {
            currentTask = new DBAsyncTask(getActivity(), this, DBAsyncTask.TASK_SINGLE_SET);
            currentTask.execute(gameSet.getId() + "");
        }
    }*/

    private DBAsyncTask.DBAsyncTaskListener taskListener = new DBAsyncTask.DBAsyncTaskListener() {
        @Override
        public void onTaskFinished(int type, ArrayList<?> objects) {
            taskFinished(type, objects);
        }

        @Override
        public void onTaskEndWithError(int type, String error) {
            taskEndWithError(type, error);
        }
    };

    protected void loadSet(MTGSet set) {
        this.gameSet = set;
        currentTask = new DBAsyncTask(getActivity(), taskListener, DBAsyncTask.TASK_SINGLE_SET);
        currentTask.execute(gameSet.getId() + "");
    }

    protected void loadSearch() {
        currentTask = new DBAsyncTask(getActivity(), taskListener, DBAsyncTask.TASK_SEARCH);
        currentTask.execute(query);
    }

    public void taskFinished(int type, ArrayList<?> result) {
        if (getActivity() == null) {
            return;
        }
        gameSet.clear();
        for (Object card : result) {
            gameSet.addCard((MTGCard) card);
        }
        populateCardsWithFilter();
        if (result.size() == CardsDatabaseHelper.LIMIT) {
            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.search_bottom, null);
            TextView moreResult = (TextView) footer.findViewById(R.id.more_result);
            moreResult.setText(getResources().getQuantityString(R.plurals.search_limit, CardsDatabaseHelper.LIMIT, CardsDatabaseHelper.LIMIT));
            listView.addFooterView(footer);
        }
        result.clear();
        progressBar.setVisibility(View.GONE);

        emptyView.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void populateCardsWithFilter() {
        cards.clear();
        SharedPreferences sharedPreferences = getSharedPreferences();
        for (MTGCard card : gameSet.getCards()) {
            boolean toAdd = false;
            if (card.getColors().contains(MTGCard.WHITE) && sharedPreferences.getBoolean(FilterHelper.FILTER_WHITE, true))
                toAdd = true;
            if (card.getColors().contains(MTGCard.BLUE) && sharedPreferences.getBoolean(FilterHelper.FILTER_BLUE, true))
                toAdd = true;
            if (card.getColors().contains(MTGCard.BLACK) && sharedPreferences.getBoolean(FilterHelper.FILTER_BLACK, true))
                toAdd = true;
            if (card.getColors().contains(MTGCard.RED) && sharedPreferences.getBoolean(FilterHelper.FILTER_RED, true))
                toAdd = true;
            if (card.getColors().contains(MTGCard.GREEN) && sharedPreferences.getBoolean(FilterHelper.FILTER_GREEN, true))
                toAdd = true;

            if (card.isALand() && sharedPreferences.getBoolean(FilterHelper.FILTER_LAND, true))
                toAdd = true;
            if (card.isAnArtifact() && sharedPreferences.getBoolean(FilterHelper.FILTER_ARTIFACT, true))
                toAdd = true;

            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_COMMON) &&
                    !sharedPreferences.getBoolean(FilterHelper.FILTER_COMMON, true))
                toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON) &&
                    !sharedPreferences.getBoolean(FilterHelper.FILTER_UNCOMMON, true))
                toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE) &&
                    !sharedPreferences.getBoolean(FilterHelper.FILTER_RARE, true))
                toAdd = false;
            if (toAdd && card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC) &&
                    !sharedPreferences.getBoolean(FilterHelper.FILTER_MYHTIC, true))
                toAdd = false;

            if (!toAdd && card.isAnEldrazi()) {
                toAdd = true;
            }

            if (toAdd) cards.add(card);

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

    public void taskEndWithError(int type, String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "card-main", error);
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


    @Override
    public void onCardSelected(MTGCard card, int position) {
        if (isASearch) position--;
        if (isASearch && listView.getFooterViewsCount() == 1 && position == cards.size()) {
            return;
        }
        if (isASearch) {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, "search pos:" + position);
        } else {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet.getName() + " pos:" + position);
        }
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, cards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.TITLE, gameSet.getName());
        startActivity(cardsView);
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        getDBActivity().openDialog("add_to_deck", AddToDeckFragment.newInstance(card));
    }
}
