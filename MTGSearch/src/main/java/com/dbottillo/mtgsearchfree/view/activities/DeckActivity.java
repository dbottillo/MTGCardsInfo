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
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.PermissionUtil;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.DecksView;
import com.dbottillo.mtgsearchfree.view.adapters.DeckCardAdapter;
import com.dbottillo.mtgsearchfree.view.adapters.DeckCardSectionAdapter;
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener;

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
            public void onCardSelected(MTGCard card, int position, View view) {
                startActivity(CardsActivity.newInstance(DeckActivity.this, deck, position));
            }

            @Override
            public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
                if (menuItem.getItemId() == R.id.action_add_one_more) {
                    TrackingManager.trackAddCardToDeck();
                    decksPresenter.addCardToDeck(deck, card, 1);

                } else if (menuItem.getItemId() == R.id.action_remove_one) {
                    TrackingManager.trackRemoveCardFromDeck();
                    decksPresenter.removeCardFromDeck(deck, card);

                } else if (menuItem.getItemId() == R.id.action_remove_all) {
                    TrackingManager.trackRemoveAllCardsFromDeck();
                    decksPresenter.removeAllCardFromDeck(deck, card);
                }
            }
        });
        deckCardSectionAdapter = new DeckCardSectionAdapter(this, deckCardAdapter);
        cardList.setAdapter(deckCardSectionAdapter);

        MTGApp.uiGraph.inject(this);
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
    public void deckLoaded(DeckBucket bucket) {
        LOG.d();
        progressBar.setVisibility(View.GONE);
        List<DeckCardSectionAdapter.Section> sections = new ArrayList<>();
        cards.clear();
        if (bucket.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            setTitle(deck.getName());
        } else {
            
            int startingPoint = 0;
            if (bucket.creatures.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_creatures) + " (" + bucket.creatures.size() + ")"));
                startingPoint += bucket.creatures.size();
                cards.addAll(bucket.creatures);
            }
            if (bucket.instantAndSorceries.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_instant_sorceries) + " (" + bucket.instantAndSorceries.size() + ")"));
                startingPoint += bucket.instantAndSorceries.size();
                cards.addAll(bucket.instantAndSorceries);
            }
            if (bucket.other.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_other) + " (" + bucket.other.size() + ")"));
                startingPoint += bucket.other.size();
                cards.addAll(bucket.other);
            }
            if (bucket.lands.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_lands) + " (" + bucket.lands.size() + ")"));
                startingPoint += bucket.lands.size();
                cards.addAll(bucket.lands);
            }
            if (bucket.side.size() > 0) {
                sections.add(new DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_sideboard) + " (" + bucket.side.size() + ")"));
                cards.addAll(bucket.side);
            }

            setTitle(deck.getName() + " (" + (bucket.sizeNoSideboard()) + "/" + bucket.sizeSideBoard() + ")");

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
        LOG.d();
        requestPermission(PermissionUtil.TYPE.WRITE_STORAGE, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionGranted() {
                Snackbar snackbar = Snackbar
                        .make(container, getString(R.string.deck_exported), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.share), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileUtil.fileNameForDeck(deck)));
                                startActivity(Intent.createChooser(intent, getString(R.string.share)));
                                TrackingManager.trackDeckExport();
                            }
                        });
                snackbar.show();
            }

            @Override
            public void permissionNotGranted() {
                exportDeckNotAllowed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.isGranted(grantResults)) {
            exportDeck();
        } else {
            exportDeckNotAllowed();
        }
    }

    private void exportDeckNotAllowed() {
        Toast.makeText(this, getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show();
        TrackingManager.trackDeckExportError();
    }

    private void editDeckName() {
        LOG.d();
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
                TrackingManager.trackEditDeck();
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
