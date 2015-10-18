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
import com.dbottillo.communication.DataManager;
import com.dbottillo.communication.events.SavedCardsEvent;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class FullScreenImageActivity extends DBActivity implements MTGCardFragment.CardConnector, ViewPager.OnPageChangeListener {

    private ArrayList<MTGCard> savedCards = new ArrayList<MTGCard>();

    private ViewPager viewPager;

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

        CardsPagerAdapter adapter = new CardsPagerAdapter(getSupportFragmentManager(), deck);
        ArrayList<MTGCard> cards = MTGApp.getCardsToDisplay();
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
        DataManager.execute(DataManager.TASK.SAVED_CARDS);
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

    public void onEventMainThread(SavedCardsEvent event) {
        if (event.isError()) {
            Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards-fullscreen", event.getErrorMessage());
        } else {
            savedCards.clear();
            for (MTGCard card : event.getResult()) {
                savedCards.add(card);
            }
            invalidateOptionsMenu();
        }
        bus.removeStickyEvent(event);
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
