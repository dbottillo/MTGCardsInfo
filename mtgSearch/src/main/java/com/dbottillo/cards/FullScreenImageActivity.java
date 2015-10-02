package com.dbottillo.cards;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.adapters.CardsPagerAdapter;
import com.dbottillo.base.DBActivity;
import com.dbottillo.base.MTGApp;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class FullScreenImageActivity extends DBActivity implements MTGCardFragment.CardConnector, DBAsyncTask.DBAsyncTaskListener, ViewPager.OnPageChangeListener {

    private ArrayList<MTGCard> savedCards = new ArrayList<MTGCard>();
    private ArrayList<MTGCard> cards;

    private ViewPager viewPager;
    private CardsPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_cards);

        setupToolbar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(MTGCardsFragment.TITLE));
            getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

        boolean deck = getIntent().getBooleanExtra(MTGCardsFragment.DECK, false);

        adapter = new CardsPagerAdapter(getSupportFragmentManager(), deck);
        cards = MTGApp.cardsToDisplay;
        if (cards != null) {
            adapter.setCards(cards);
            adapter.setFullScreen(true);
            viewPager.setAdapter(adapter);
            int position = getIntent().getIntExtra(MTGCardsFragment.POSITION, 0);
            viewPager.setCurrentItem(position);
            viewPager.setOnPageChangeListener(this);

            refreshColor(position);
        } else {
            finish();
        }
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
        finishWithResult();
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_CARD, "fullscreen", "tap_on_image_close");
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
            savedCards.add((MTGCard) card);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards-fullscreen", error);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        refreshColor(position);
    }

    @Override

    public void onPageScrollStateChanged(int state) {

    }

    private void refreshColor(int pos) {
        //MTGCard card = (MTGCard) cards.get(pos);
        //pagerTabStrip.setBackgroundColor(card.getMtgColor(getActivity()));
        //MaterialWrapper.setStatusBarColor(getActivity(), card.getMtgColor(getActivity()));
        //MaterialWrapper.setNavigationBarColor(this, card.getMtgColor(this));
        //((CardsActivity) getActivity()).setToolbarColor(card.getMtgColor(getActivity()));
    }
}
