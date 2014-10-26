package com.dbottillo.cards;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.adapters.CardListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.GameCard;
import com.dbottillo.resources.GameSet;
import com.dbottillo.resources.HSSet;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;
import com.google.android.gms.ads.AdListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGSetFragment extends DBFragment implements DBAsyncTask.DBAsyncTaskListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String SET_CHOSEN = "set_chosen";
    private static final String SEARCH = "search";
    private static DBAsyncTask currentTask = null;
    boolean isASearch = false;
    private GameSet gameSet;
    private ListView listView;
    private TextView emptyView;
    private ArrayList<GameCard> cards;
    private CardListAdapter adapter;
    private SmoothProgressBar progressBar;
    private String query;

    public MTGSetFragment() {
    }

    public static MTGSetFragment newInstance(GameSet set) {
        MTGSetFragment fragment = new MTGSetFragment();
        Bundle args = new Bundle();
        args.putParcelable(SET_CHOSEN, set);
        fragment.setArguments(args);
        return fragment;
    }

    public static MTGSetFragment newInstance(String query) {
        MTGSetFragment fragment = new MTGSetFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_search);

        gameSet = getArguments().getParcelable(SET_CHOSEN);
        if (gameSet == null) {
            isASearch = true;
            query = getArguments().getString(SEARCH);
            if (BuildConfig.magic) {
                gameSet = new MTGSet(-1);
            } else {
                gameSet = new HSSet(-1);
            }
            gameSet.setName(query);
        }

        listView = (ListView) rootView.findViewById(R.id.set_list);

        if (isASearch) {
            View header = inflater.inflate(R.layout.search_header, null);
            TextView searchQueryText = (TextView) header.findViewById(R.id.search_query);
            searchQueryText.setText(query);
            listView.addHeaderView(header);
        }

        cards = new ArrayList<GameCard>();
        adapter = new CardListAdapter(getActivity(), cards, isASearch);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        if (!getApp().isPremium() && !BuildConfig.magic) {
            createAdView("ca-app-pub-8119815713373556/4408152412");
            getAdView().setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    // Save app state before going to the ad overlay.
                }
            });

            FrameLayout layout = (FrameLayout) rootView.findViewById(R.id.banner_container);
            layout.addView(getAdView());

            getAdView().loadAd(createAdRequest());
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentTask != null) {
            currentTask.attach(getActivity(), this);
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
    }

    @Override
    public void onTaskFinished(ArrayList<?> result) {
        if (getActivity() == null) {
            return;
        }
        gameSet.clear();
        int i = 0;
        boolean premium = getApp().isPremium();
        for (Object card : result) {
            //if (premium  || !isASearch || (!premium && i < 3)) {
            gameSet.addCard((GameCard) card);
            //}
            //if (isASearch && !premium && i >= 3) {
            //    break;
            //}
            i++;
        }
        populateCardsWithFilter();
        //int more = result.size() - 3;
        //if (result.size() == MTGDatabaseHelper.LIMIT || (isASearch && more > 0 && !premium)) {
        if (result.size() == MTGDatabaseHelper.LIMIT) {
            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.search_bottom, null);
            TextView moreResult = (TextView) footer.findViewById(R.id.more_result);
            Button openPlayStore = (Button) footer.findViewById(R.id.open_play_store);
            /*if (!premium && isASearch && more > 0) {
                moreResult.setText(getString(R.string.more_result, more));
                openPlayStore.setOnClickListener(this);
            }else{*/
            moreResult.setText(getString(R.string.search_limit, MTGDatabaseHelper.LIMIT));
            openPlayStore.setVisibility(View.GONE);
            footer.findViewById(R.id.open_play_store_text).setVisibility(View.GONE);
            //}
            listView.addFooterView(footer);
        }
        result.clear();
        progressBar.setVisibility(View.GONE);

        emptyView.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void populateCardsWithFilter() {
        cards.clear();
        SharedPreferences sharedPreferences = getSharedPreferences();
        for (GameCard c : gameSet.getCards()) {
            boolean toAdd = false;
            if (BuildConfig.magic) {
                MTGCard card = (MTGCard) c;
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
            } else {
                toAdd = true;
            }
            if (toAdd) cards.add(c);
            if (BuildConfig.magic) {
                Collections.sort(cards, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        MTGCard card = (MTGCard) o1;
                        MTGCard card2 = (MTGCard) o2;
                        return card.compareTo(card2);
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(0);
    }

    @Override
    public void onTaskEndWithError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isASearch) position--;
        if (isASearch && listView.getFooterViewsCount() == 1 && position == cards.size()) {
            return;
        }
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet.getName() + " pos:" + position);
        Intent cardsView = new Intent(getActivity(), CardsActivity.class);
        cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, cards);
        cardsView.putExtra(MTGCardsFragment.POSITION, position);
        cardsView.putExtra(MTGCardsFragment.SET_NAME, gameSet.getName());
        startActivity(cardsView);
    }

    public void refreshUI() {
        populateCardsWithFilter();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_play_store) {
            openPlayStore();
        }
    }
}
