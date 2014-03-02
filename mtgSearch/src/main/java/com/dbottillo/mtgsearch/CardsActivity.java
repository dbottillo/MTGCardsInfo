package com.dbottillo.mtgsearch;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dbottillo.adapters.MTGSetSpinnerAdapter;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import java.util.ArrayList;

public class CardsActivity extends DBActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
