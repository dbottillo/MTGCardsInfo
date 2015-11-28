package com.dbottillo.cards;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.RandomCardsEvent;
import com.dbottillo.communication.events.SavedCardsEvent;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardLuckyActivity extends DBActivity implements MTGCardFragment.CardConnector {

    public static final String CARD = "CARD";

    private ArrayList<MTGCard> savedCards = new ArrayList<MTGCard>();

    private ArrayList<MTGCard> luckyCards;

    MTGCardFragment cardFragment;

    private boolean isLoading = false;
    private boolean loadCardAfterDatabase = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_card);

        setupToolbar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lucky_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.btn_lucky_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRandomCard();
            }
        });

        if (savedInstanceState == null) {
            luckyCards = new ArrayList<>();
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(CARD) != null) {
                luckyCards.add((MTGCard) getIntent().getExtras().getParcelable(CARD));
                loadCard();
            } else {
                loadRandomCard();
            }
        } else {
            luckyCards = savedInstanceState.getParcelableArrayList("luckyCards");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("luckyCards", luckyCards);
    }

    private void loadRandomCard() {
        if (luckyCards.size() > 0) {
            // execute from memory
            loadCard();
            return;
        }
        if (!isLoading) {
            isLoading = true;
            loadCardAfterDatabase = true;
            DataManager.execute(DataManager.TASK.RANDOM_CARDS, 4);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.execute(DataManager.TASK.SAVED_CARDS, false);
    }

    @Override
    public String getPageTrack() {
        return "/lucky-card";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isCardSaved(MTGCard card) {
        boolean isSaved = false;
        for (MTGCard savedCard : savedCards) {
            if (savedCard.getId() == card.getId()) {
                isSaved = true;
                break;
            }
        }
        return isSaved;
    }

    @Override
    public void saveCard(MTGCard card) {
        DataManager.execute(DataManager.TASK.SAVE_CARD, card);
        savedCards.add(card);
        invalidateOptionsMenu();
    }

    @Override
    public void removeCard(MTGCard card) {
        DataManager.execute(DataManager.TASK.UN_SAVE_CARD, card);
        for (MTGCard savedCard : savedCards) {
            if (savedCard.getId() == card.getId()) {
                savedCards.remove(savedCard);
                break;
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public void tapOnImage(int position) {
        loadRandomCard();
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_LUCKY, "tap_on_image");
    }

    private void loadCard() {
        MTGCard card = luckyCards.remove(0);
        cardFragment = MTGCardFragment.newInstance(card, 0, false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, cardFragment)
                .commit();
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_LUCKY, card.getName());
        if (luckyCards.size() <= 2) {
            // pre-fetch more
            loadCardAfterDatabase = false;
            DataManager.execute(DataManager.TASK.RANDOM_CARDS, 4);
        }
    }

    public void onEventMainThread(RandomCardsEvent event) {
        isLoading = false;
        if (event.isError()) {
            Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "get-lucky", event.getErrorMessage());
        } else {
            for (MTGCard card : event.getResult()) {
                luckyCards.add(card);
                if (card.getImage() != null) {
                    // pre-fetch images
                    Picasso.with(this).load(card.getImage()).fetch();
                }
            }
            if (loadCardAfterDatabase && !isFinishing()) {
                loadCard();
            }
        }
        bus.removeStickyEvent(event);
    }

    public void onEventMainThread(SavedCardsEvent event) {
        if (event.isError()) {
            Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-card-lucky", event.getErrorMessage());
        } else {
            savedCards.clear();
            for (MTGCard card : event.getResult()) {
                savedCards.add(card);
            }
            invalidateOptionsMenu();
        }
        bus.removeStickyEvent(event);
    }
}
