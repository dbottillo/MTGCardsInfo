package com.dbottillo.cards;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.GameCard;

public class FullScreenImageActivity extends DBActivity implements MTGCardFragment.DatabaseConnector {

    private DB40Helper db40Helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGCardsFragment.newInstance(getIntent().<GameCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS),
                        getIntent().getIntExtra(MTGCardsFragment.POSITION, 0),
                        getIntent().getStringExtra(MTGCardsFragment.SET_NAME)))
                .commit();
        }

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
}
