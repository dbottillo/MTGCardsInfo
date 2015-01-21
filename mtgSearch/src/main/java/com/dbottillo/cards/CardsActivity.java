package com.dbottillo.cards;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.GameCard;

import java.util.ArrayList;

public class CardsActivity extends DBActivity implements MTGCardFragment.CardConnector, DBAsyncTask.DBAsyncTaskListener {

    private ArrayList<GameCard> savedCards = new ArrayList<GameCard>();

    public static final int FULLSCREEN_CODE = 100;

    MTGCardsFragment cardsFragment;
    String setName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        setupToolbar();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(0);
        }

        cardsFragment = (MTGCardsFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (cardsFragment == null) {
            setName = getIntent().getStringExtra(MTGCardsFragment.SET_NAME);
            cardsFragment = MTGCardsFragment.newInstance(getIntent().<GameCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS),
                    getIntent().getIntExtra(MTGCardsFragment.POSITION, 0),
                   setName);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, cardsFragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SAVED).execute();
    }

    @Override
    public String getPageTrack() {
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
    public boolean isCardSaved(GameCard card) {
        boolean isSaved = false;
        for (GameCard savedCard : savedCards) {
            if (savedCard.getId() == card.getId()) {
                isSaved = true;
                break;
            }
        }
        return isSaved;
    }

    @Override
    public void saveCard(GameCard card) {
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SAVE_CARD).execute(card);
        savedCards.add(card);
        invalidateOptionsMenu();
    }

    @Override
    public void removeCard(GameCard card) {
        new DBAsyncTask(this, this, DBAsyncTask.TASK_REMOVE_CARD).execute(card);
        for (GameCard savedCard : savedCards) {
            if (savedCard.getId() == card.getId()) {
                savedCards.remove(savedCard);
                break;
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public void tapOnImage(int position) {
        openFullScreen(position);
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_CARD, "fullscreen", "tap_on_image");
    }

    public void openFullScreen(int currentItem) {
        Intent fullScreen = new Intent(this, FullScreenImageActivity.class);
        fullScreen.putExtra(MTGCardsFragment.CARDS, cardsFragment.getCards());
        fullScreen.putExtra(MTGCardsFragment.POSITION, currentItem);
        fullScreen.putExtra(MTGCardsFragment.SET_NAME, setName);
        startActivityForResult(fullScreen, CardsActivity.FULLSCREEN_CODE);
    }

    @Override
    public void onTaskFinished(int type, ArrayList<?> objects) {
        savedCards.clear();
        for (Object card : objects) {
            savedCards.add((GameCard) card);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards", error);
    }

    public void setBackgroundToolbar(int mtgColor) {
        toolbar.setBackgroundColor(mtgColor);
    }
}
