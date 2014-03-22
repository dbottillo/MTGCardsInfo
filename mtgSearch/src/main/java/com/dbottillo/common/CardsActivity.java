package com.dbottillo.common;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.base.DBActivity;
import com.dbottillo.R;
import com.dbottillo.resources.MTGCard;

public class CardsActivity extends DBActivity {

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

}
