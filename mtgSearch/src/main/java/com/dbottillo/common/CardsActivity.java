package com.dbottillo.common;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.base.DBActivity;
import com.dbottillo.R;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.MTGCard;

public class CardsActivity extends DBActivity implements MTGCardFragment.DatabaseConnector {

    private DB40Helper db40Helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGCardsFragment.newInstance(getIntent().<MTGCard>getParcelableArrayListExtra(MTGCardsFragment.CARDS),
                        getIntent().getIntExtra(MTGCardsFragment.POSITION, 0),
                        getIntent().getStringExtra(MTGCardsFragment.SET_NAME)))
                .commit();
        }

        db40Helper = new DB40Helper(this);
        db40Helper.openDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db40Helper.closeDb();
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
    public boolean isCardSaved(MTGCard card) {
        return db40Helper.isCardStored(card);
    }

    @Override
    public void saveCard(MTGCard card) {
        db40Helper.storeCard(card);
    }

    @Override
    public void removeCard(MTGCard card) {
        db40Helper.removeCard(card);
    }
}
