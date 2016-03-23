package com.dbottillo.mtgsearchfree.view.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.DeckCardAdapter;
import com.dbottillo.mtgsearchfree.adapters.DeckCardSectionAdapter;
import com.dbottillo.mtgsearchfree.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.view.DecksView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class DeckActivity extends BasicActivity implements DecksView {

    @Inject
    DecksPresenter decksPresenter;

    @Bind(R.id.container)
    View container;

    @Bind(R.id.empty_view)
    TextView emptyView;

    @Bind(R.id.progress)
    SmoothProgressBar progressBar;

    @Bind(R.id.card_list)
    RecyclerView cardList;

    Deck deck;
    private ArrayList<MTGCard> cards;
    private DeckCardSectionAdapter deckCardSectionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        ButterKnife.bind(this);

        setupToolbar();

        deck = getIntent().getParcelableExtra("deck");
        setTitle(deck.getName());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyView.setText(R.string.empty_deck);

        cards = new ArrayList<>();

        cardList.setHasFixedSize(true);
        cardList.setLayoutManager(new LinearLayoutManager(this));

        DeckCardAdapter deckCardAdapter = new DeckCardAdapter(this, cards, R.menu.deck_card, new OnCardListener() {
            @Override
            public void onCardSelected(MTGCard card, int position) {
                MTGApp.cardsToDisplay = cards;
                startActivity(CardsActivity.newInstance(DeckActivity.this, deck, position));
            }

            @Override
            public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
                if (menuItem.getItemId() == R.id.action_add_one_more) {
                    TrackingHelper.getInstance(DeckActivity.this).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_ONE_MORE);
                    decksPresenter.addCardToDeck(deck, card, 1);

                } else if (menuItem.getItemId() == R.id.action_remove_one) {
                    TrackingHelper.getInstance(DeckActivity.this).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_REMOVE_ONE);
                    decksPresenter.removeCardFromDeck(deck, card);

                } else if (menuItem.getItemId() == R.id.action_remove_all) {
                    TrackingHelper.getInstance(DeckActivity.this).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_REMOVE_ALL);
                    decksPresenter.removeAllCardFromDeck(deck, card);
                }
            }
        });
        deckCardSectionAdapter = new DeckCardSectionAdapter(this, deckCardAdapter);
        cardList.setAdapter(deckCardSectionAdapter);

        MTGApp.dataGraph.inject(this);
        decksPresenter.init(this);
        decksPresenter.loadDeck(deck);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_export) {
            exportDeck();
            return true;
        } else if (id == R.id.action_edit) {
            editDeckName();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getPageTrack() {
        return null;
    }

    @Override
    public void decksLoaded(List<Deck> decks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deckLoaded(List<MTGCard> newCards) {
        progressBar.setVisibility(View.GONE);
        List<DeckCardSectionAdapter.Section> sections = new ArrayList<>();
        cards.clear();
        if (newCards.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            setTitle(deck.getName());
        } else {
            ArrayList<MTGCard> creatures = new ArrayList<>();
            ArrayList<MTGCard> instantAndSorceries = new ArrayList<>();
            ArrayList<MTGCard> other = new ArrayList<>();
            ArrayList<MTGCard> lands = new ArrayList<>();
            ArrayList<MTGCard> side = new ArrayList<>();
            int nCreatures = 0, nInstanceSorceries = 0, nOther = 0, nLands = 0, nSide = 0;
            for (MTGCard card : newCards) {
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

            setTitle(deck.getName() + " (" + (nCreatures + nInstanceSorceries + nOther + nLands) + "/" + nSide + ")");

        }
        DeckCardSectionAdapter.Section[] dummy = new DeckCardSectionAdapter.Section[sections.size()];
        deckCardSectionAdapter.setSections(sections.toArray(dummy));
        deckCardSectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void exportDeck() {
        if (FileUtil.downloadDeckToSdCard(this, deck, cards)) {
            Snackbar snackbar = Snackbar
                    .make(container, getString(R.string.deck_exported), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.share), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileUtil.fileNameForDeck(deck)));
                            startActivity(Intent.createChooser(intent, getString(R.string.share)));
                            TrackingHelper.getInstance(DeckActivity.this).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_SHARE);
                        }
                    });
            snackbar.show();
        } else {
            Toast.makeText(this, getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, TrackingHelper.UA_ACTION_EXPORT, "[deck] impossible to create folder");
        }
    }

    private void editDeckName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.MTGDialogTheme);

        alert.setTitle(getString(R.string.edit_deck));

        final EditText input = new EditText(this);
        input.setText(deck.getName());
        input.setSelection(deck.getName().length());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                decksPresenter.editDeck(deck, value);
                TrackingHelper.getInstance(DeckActivity.this).trackEvent(TrackingHelper.UA_CATEGORY_DECK, "editName");
                deck.setName(value);
                setTitle(deck.getName());
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
