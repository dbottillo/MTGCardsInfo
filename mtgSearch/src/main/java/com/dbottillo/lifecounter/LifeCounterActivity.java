package com.dbottillo.lifecounter;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;

public class LifeCounterActivity extends DBActivity {

    private LifeCounterFragment lifeCounterFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_counter);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_life_counter);

        if (savedInstanceState == null) {
            lifeCounterFragment = LifeCounterFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, lifeCounterFragment)
                    .commit();
        } else {
            lifeCounterFragment = (LifeCounterFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }

    @Override
    public String getPageTrack() {
        return "/life_counter";
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
    public void onBackPressed() {
        if (!lifeCounterFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
