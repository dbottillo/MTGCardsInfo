package com.dbottillo.cards;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.GameCard;

public class CardsActivity extends DBActivity implements MTGCardFragment.DatabaseConnector {

    private DB40Helper db40Helper;

    public static final int FULLSCREEN_CODE = 100;

    MTGCardsFragment cardsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        cardsFragment = (MTGCardsFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (cardsFragment == null) {
            cardsFragment = MTGCardsFragment.newInstance(getIntent().<GameCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS),
                    getIntent().getIntExtra(MTGCardsFragment.POSITION, 0),
                    getIntent().getStringExtra(MTGCardsFragment.SET_NAME));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, cardsFragment)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FULLSCREEN_CODE && resultCode == RESULT_OK) {
            cardsFragment.goTo(data.getIntExtra(MTGCardsFragment.POSITION, 0));
        }
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

    public void openFullScreen(int currentItem) {
        Intent fullScreen = new Intent(this, FullScreenImageActivity.class);
        fullScreen.putExtra(MTGCardsFragment.CARDS, cardsFragment.getCards());
        fullScreen.putExtra(MTGCardsFragment.POSITION, currentItem);
        startActivityForResult(fullScreen, CardsActivity.FULLSCREEN_CODE);
    }
}
