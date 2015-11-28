package com.dbottillo.cards;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.SavedCardsEvent;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class CardsActivity extends DBActivity implements MTGCardFragment.CardConnector {

    private ArrayList<MTGCard> savedCards = new ArrayList<>();

    public static final int FULLSCREEN_CODE = 100;

    MTGCardsFragment cardsFragment;
    String title;
    boolean deck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        setupToolbar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSupportActionBar().setElevation(0);
            }
        }
        cardsFragment = (MTGCardsFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (cardsFragment == null) {
            title = getIntent().getStringExtra(MTGCardsFragment.TITLE);
            deck = getIntent().getBooleanExtra(MTGCardsFragment.DECK, false);
            cardsFragment = MTGCardsFragment.newInstance(getIntent().getIntExtra(MTGCardsFragment.POSITION, 0),
                    title, deck
            );
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, cardsFragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.execute(DataManager.TASK.SAVED_CARDS, false);
    }

    @Override
    public String getPageTrack() {
        if (deck) {
            return "/deck";
        }
        return "/cards";
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FULLSCREEN_CODE && resultCode == RESULT_OK) {
            cardsFragment.goTo(data.getIntExtra(MTGCardsFragment.POSITION, 0));
        }
    }

    @Override
    public boolean isCardSaved(MTGCard card) {
        boolean isSaved = false;
        for (MTGCard savedCard : savedCards) {
            if (savedCard.getMultiVerseId() == card.getMultiVerseId()) {
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
            if (savedCard.getMultiVerseId() == card.getMultiVerseId()) {
                savedCards.remove(savedCard);
                break;
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public void tapOnImage(int position) {
        openFullScreen(position);
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_CARD, "fullscreen", "tap_on_image");
    }

    public void openFullScreen(int currentItem) {
        Intent fullScreen = new Intent(this, FullScreenImageActivity.class);
        fullScreen.putExtra(MTGCardsFragment.POSITION, currentItem);
        fullScreen.putExtra(MTGCardsFragment.TITLE, title);
        fullScreen.putExtra(MTGCardsFragment.DECK, deck);
        startActivityForResult(fullScreen, CardsActivity.FULLSCREEN_CODE);
    }

    public void onEventMainThread(SavedCardsEvent event) {
        if (event.isError()) {
            Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards", event.getErrorMessage());
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
