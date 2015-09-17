package com.dbottillo.decks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.adapters.DeckCardAdapter;
import com.dbottillo.adapters.DeckCardSectionAdapter;
import com.dbottillo.adapters.OnCardListener;
import com.dbottillo.base.DBFragment;
import com.dbottillo.cards.CardsActivity;
import com.dbottillo.cards.MTGCardsFragment;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.resources.Deck;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;
import java.util.List;

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
    private Deck deck;
    private TextView emptyView;
    private SmoothProgressBar progressBar;
    private DeckCardSectionAdapter deckCardSectionAdapter;

    private Loader deckLoader;

    private DeckDataSource deckDataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deck, container, false);

        deck = getArguments().getParcelable("deck");

        setActionBarTitle(deck.getName());

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.card_list);

        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_deck);

        cards = new ArrayList<>();

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        deckDataSource = new DeckDataSource(getActivity());
        deckDataSource.open();

        DeckCardAdapter deckCardAdapter = new DeckCardAdapter(getContext(), cards, R.menu.deck_card, new OnCardListener() {
            @Override
            public void onCardSelected(MTGCard card, int position) {
                Intent cardsView = new Intent(getActivity(), CardsActivity.class);
                cardsView.putParcelableArrayListExtra(MTGCardsFragment.CARDS, cards);
                cardsView.putExtra(MTGCardsFragment.POSITION, position);
                cardsView.putExtra(MTGCardsFragment.TITLE, deck.getName());
                cardsView.putExtra(MTGCardsFragment.DECK, true);
                startActivity(cardsView);
            }

            @Override
            public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
                if (menuItem.getItemId() == R.id.action_add_one_more) {
                    deckDataSource.addCardToDeck(deck.getId(), card, 1, card.isSideboard());
                } else if (menuItem.getItemId() == R.id.action_remove_one) {
                    deckDataSource.addCardToDeck(deck.getId(), card, -1, card.isSideboard());
                } else {
                    deckDataSource.removeCardFromDeck(deck.getId(), card, card.isSideboard());
                }
                forceReload();
            }
        });
        deckCardSectionAdapter = new DeckCardSectionAdapter(getContext(), deckCardAdapter);
        listView.setAdapter(deckCardSectionAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        forceReload();
    }

    private void forceReload() {
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
        progressBar.setVisibility(View.GONE);
        List<DeckCardSectionAdapter.Section> sections = new ArrayList<>();
        if (data.size() == 0) {
            cards.clear();
            emptyView.setVisibility(View.VISIBLE);
            setActionBarTitle(deck.getName());
        } else {
            cards.clear();
            ArrayList<MTGCard> creatures = new ArrayList<>();
            ArrayList<MTGCard> instantAndSorceries = new ArrayList<>();
            ArrayList<MTGCard> other = new ArrayList<>();
            ArrayList<MTGCard> lands = new ArrayList<>();
            ArrayList<MTGCard> side = new ArrayList<>();
            int nCreatures = 0, nInstanceSorceries = 0, nOther = 0, nLands = 0, nSide = 0;
            for (MTGCard card : data) {
                if (card.isSideboard()) {
                    nSide += card.getQuantity();
                    side.add(card);
                } else if (card.isALand()) {
                    nLands += card.getQuantity();
                    lands.add(card);
                } else if (card.getTypes().contains("Creature")) {
                    nCreatures += card.getQuantity();
                    creatures.add(card);
                } else if (card.getTypes().contains("Instant") || card.getTypes().contains("Sorcery")) {
                    nInstanceSorceries += card.getQuantity();
                    instantAndSorceries.add(card);
                } else {
                    nOther += card.getQuantity();
                    other.add(card);
                }
            }
            int startingPoint = 0;
            if (creatures.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_creatures) + " (" + nCreatures + ")"));
                startingPoint += creatures.size();
                cards.addAll(creatures);
            }
            if (instantAndSorceries.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_instant_sorceries) + " (" + nInstanceSorceries + ")"));
                startingPoint += instantAndSorceries.size();
                cards.addAll(instantAndSorceries);
            }
            if (other.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_other) + " (" + nOther + ")"));
                startingPoint += other.size();
                cards.addAll(other);
            }
            if (lands.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_lands) + " (" + nLands + ")"));
                startingPoint += lands.size();
                cards.addAll(lands);
            }
            if (side.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_sideboard) + " (" + nSide + ")"));
                cards.addAll(side);
            }

            setActionBarTitle(deck.getName() + " (" + (nCreatures + nInstanceSorceries + nOther + nLands) + "/" + nSide + ")");
        }
        DeckCardSectionAdapter.Section[] dummy = new DeckCardSectionAdapter.Section[sections.size()];
        deckCardSectionAdapter.setSections(sections.toArray(dummy));
        deckCardSectionAdapter.notifyDataSetChanged();
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
