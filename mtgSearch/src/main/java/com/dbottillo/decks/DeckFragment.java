package com.dbottillo.decks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.adapters.CardListAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.resources.Deck;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DeckFragment extends DBFragment implements LoaderManager.LoaderCallbacks<ArrayList<MTGCard>> {

    public static DeckFragment newInstance(Deck deck) {
        DeckFragment deckFragment = new DeckFragment();
        Bundle args = new Bundle();
        args.putParcelable("deck", deck);
        deckFragment.setArguments(args);
        return deckFragment;
    }

    private ArrayList<MTGCard> cards;
    private CardListAdapter cardListAdapter;
    private Deck deck;
    private ListView listView;
    private TextView emptyView;
    private SmoothProgressBar progressBar;

    private Loader deckLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deck, container, false);

        deck = getArguments().getParcelable("deck");

        setActionBarTitle(deck.getName());

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        listView = (ListView) rootView.findViewById(R.id.card_list);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_saved);

        cards = new ArrayList<>();

        cardListAdapter = new CardListAdapter(getActivity(), cards, false);
        listView.setAdapter(cardListAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        deckLoader = getLoaderManager().initLoader(102, null, this);
        deckLoader.forceLoad();
    }

    @Override
    public void onStop() {
        super.onStop();
        deckLoader.stopLoading();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public String getPageTrack() {
        return "/deck";
    }

    @Override
    public Loader<ArrayList<MTGCard>> onCreateLoader(int id, Bundle args) {
        return new DeckLoader(getActivity(), deck);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MTGCard>> loader, ArrayList<MTGCard> data) {
        cards.clear();
        for (MTGCard card : data) {
            cards.add(card);
        }
        cardListAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    static class DeckLoader extends AsyncTaskLoader<ArrayList<MTGCard>> {

        Deck deck;

        public DeckLoader(Context context, Deck deck) {
            super(context);
            this.deck = deck;
        }

        public ArrayList<MTGCard> loadInBackground() {
            DeckDataSource deckDataSource = new DeckDataSource(getContext());
            deckDataSource.open();
            return deckDataSource.getCards(deck);
        }
    }

}
