package com.dbottillo.cards;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.LOG;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardLuckyActivity extends DBActivity implements MTGCardFragment.CardConnector, DBAsyncTask.DBAsyncTaskListener {

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

        getSupportActionBar().setTitle(getString(R.string.lucky_title));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            // load from memory
            loadCard();
            return;
        }
        if (!isLoading) {
            isLoading = true;
            loadCardAfterDatabase = true;
            new DBAsyncTask(this, this, DBAsyncTask.TASK_RANDOM_CARD).execute(4);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SAVED).execute();
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
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SAVE_CARD).execute(card);
        savedCards.add(card);
        invalidateOptionsMenu();
    }

    @Override
    public void removeCard(MTGCard card) {
        new DBAsyncTask(this, this, DBAsyncTask.TASK_REMOVE_CARD).execute(card);
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

    @Override
    public void onTaskFinished(int type, ArrayList<?> objects) {
        if (!onSaveInstanceStateCalled) {
            if (type != DBAsyncTask.TASK_RANDOM_CARD) {
                savedCards.clear();
                for (Object card : objects) {
                    savedCards.add((MTGCard) card);
                }
                invalidateOptionsMenu();
            } else {
                isLoading = false;
                for (Object obj : objects) {
                    MTGCard card = (MTGCard) obj;
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
        }
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
            new DBAsyncTask(this, this, DBAsyncTask.TASK_RANDOM_CARD).execute(4);
        }
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        if (type == DBAsyncTask.TASK_RANDOM_CARD) {
            isLoading = false;
        }
        Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, type != DBAsyncTask.TASK_RANDOM_CARD ? "saved-card-lucky" : "get-lucky", error);
    }
}
