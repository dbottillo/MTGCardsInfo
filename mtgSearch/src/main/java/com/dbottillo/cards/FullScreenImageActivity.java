package com.dbottillo.cards;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.CardsPagerAdapter;
import com.dbottillo.base.DBActivity;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.GameCard;

import java.util.ArrayList;

public class FullScreenImageActivity extends DBActivity implements MTGCardFragment.DatabaseConnector, DBAsyncTask.DBAsyncTaskListener {

    private ArrayList<GameCard> savedCards = new ArrayList<GameCard>();

    private ViewPager viewPager;
    private CardsPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_cards);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55222222")));

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new CardsPagerAdapter(getSupportFragmentManager());
        adapter.setCards(getIntent().<GameCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS));
        adapter.setFullScreen(true);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra(MTGCardsFragment.POSITION, 0));
    }

    @Override
    public void onResume() {
        super.onResume();
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SAVED).execute();
    }

    @Override
    public String getPageTrack() {
        return "/fullscreen_cards";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishWithResult();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        finishWithResult();
    }

    private void finishWithResult() {
        Intent res = new Intent();
        res.putExtra(MTGCardsFragment.POSITION, viewPager.getCurrentItem());
        setResult(RESULT_OK, res);
        finish();
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
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards-fullscreen", error);
    }
}
