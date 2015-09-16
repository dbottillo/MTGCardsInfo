package com.dbottillo.decks;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.resources.Deck;

public class DeckActivity extends DBActivity {

    Deck deck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        setupToolbar();

        deck = getIntent().getParcelableExtra("deck");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DeckFragment.newInstance(deck))
                    .commit();
        }
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
    public String getPageTrack() {
        return "deck/"+deck.getName();
    }
}
