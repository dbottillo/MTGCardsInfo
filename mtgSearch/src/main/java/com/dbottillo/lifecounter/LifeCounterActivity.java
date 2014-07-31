package com.dbottillo.lifecounter;

import android.os.Bundle;
import android.view.MenuItem;

import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.saved.SavedFragment;

public class LifeCounterActivity extends DBActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_counter);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.action_life_counter);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LifeCounterFragment.newInstance())
                .commit();
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

}
