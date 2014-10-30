package com.dbottillo.cards;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.Window;

import com.dbottillo.R;
import com.dbottillo.adapters.CardsPagerAdapter;
import com.dbottillo.base.DBActivity;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.GameCard;

public class FullScreenImageActivity extends DBActivity implements MTGCardFragment.DatabaseConnector {

    private DB40Helper db40Helper;

    private ViewPager viewPager;
    private CardsPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_cards);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("");
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55222222")));

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new CardsPagerAdapter(getSupportFragmentManager());
        adapter.setCards(getIntent().<GameCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS));
        adapter.setFullScreen(true);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra(MTGCardsFragment.POSITION, 0));

        db40Helper = DB40Helper.getInstance(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        db40Helper.openDb();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db40Helper.closeDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db40Helper = null;
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
        return db40Helper.isCardStored(card);
    }

    @Override
    public void saveCard(GameCard card) {
        db40Helper.storeCard(card);
    }

    @Override
    public void removeCard(GameCard card) {
        db40Helper.removeCard(card);
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
}
