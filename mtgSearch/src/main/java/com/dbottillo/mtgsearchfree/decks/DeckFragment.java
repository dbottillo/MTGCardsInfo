package com.dbottillo.mtgsearchfree.decks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.DeckCardAdapter;
import com.dbottillo.mtgsearchfree.adapters.DeckCardSectionAdapter;
import com.dbottillo.mtgsearchfree.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.cards.CardsActivity;
import com.dbottillo.mtgsearchfree.cards.MTGCardsFragment;
import com.dbottillo.mtgsearchfree.communication.DataManager;
import com.dbottillo.mtgsearchfree.communication.events.DeckEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.Deck;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DeckFragment extends BasicFragment implements LoaderManager.LoaderCallbacks<ArrayList<MTGCard>> {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deck, container, false);

        deck = getArguments().getParcelable("deck");

        setActionBarTitle(deck.getName());
        setHasOptionsMenu(true);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.card_list);

        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_deck);

        cards = new ArrayList<>();

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DeckCardAdapter deckCardAdapter = new DeckCardAdapter(getContext(), cards, R.menu.deck_card, new OnCardListener() {
            @Override
            public void onCardSelected(MTGCard card, int position) {
                Intent cardsView = new Intent(getActivity(), CardsActivity.class);
                MTGApp.Companion.setCardsToDisplay(cards);
                cardsView.putExtra(MTGCardsFragment.POSITION, position);
                cardsView.putExtra(MTGCardsFragment.TITLE, deck.getName());
                cardsView.putExtra(MTGCardsFragment.DECK, true);
                startActivity(cardsView);
            }

            @Override
            public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
                if (menuItem.getItemId() == R.id.action_add_one_more) {
                    TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_ONE_MORE);
                    DataManager.execute(DataManager.TASK.EDIT_DECK, true, deck.getId(), card, 1, card.isSideboard());

                } else if (menuItem.getItemId() == R.id.action_remove_one) {
                    TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_REMOVE_ONE);
                    DataManager.execute(DataManager.TASK.EDIT_DECK, true, deck.getId(), card, -1, card.isSideboard());

                } else if (menuItem.getItemId() == R.id.action_remove_all) {
                    TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_REMOVE_ALL);
                    DataManager.execute(DataManager.TASK.EDIT_DECK, false, deck.getId(), card, card.isSideboard());
                }
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

    public void onEventMainThread(DeckEvent event) {
        forceReload();
        getBus().removeStickyEvent(event);
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
    public String getPageTrack() {
        return "deck/" + deck.getName();
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
                } else if (card.isLand()) {
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
            CardsInfoDbHelper dbHelper = CardsInfoDbHelper.getInstance(getContext());
            return DeckDataSource.getCards(dbHelper.getReadableDatabase(), deck);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.deck, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_export) {
            exportDeck();
            return true;
        } else if (i1 == R.id.action_edit) {
            editDeckName();
            return true;
        }

        return false;
    }

    private void exportDeck() {
        if (FileUtil.downloadDeckToSdCard(getApp(), deck, cards)) {
            if (this.getView() != null) {
                Snackbar snackbar = Snackbar
                        .make(getView(), getString(R.string.deck_exported), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.share), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileUtil.fileNameForDeck(deck)));
                                startActivity(Intent.createChooser(intent, getString(R.string.share)));
                                TrackingHelper.getInstance(getContext()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_SHARE);
                            }
                        });
                snackbar.show();
            }
        } else {
            Toast.makeText(getContext(), getContext().getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(getContext()).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, TrackingHelper.UA_ACTION_EXPORT, "[deck] impossible to create folder");
        }
    }

    private void editDeckName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MTGDialogTheme);

        alert.setTitle(getString(R.string.edit_deck));

        final EditText input = new EditText(getActivity());
        input.setText(deck.getName());
        input.setSelection(deck.getName().length());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                DataManager.execute(DataManager.TASK.EDIT_DECK_NAME, deck.getId(), value);
                TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_DECK, "editName");
                deck.setName(value);
                setActionBarTitle(deck.getName());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

}
