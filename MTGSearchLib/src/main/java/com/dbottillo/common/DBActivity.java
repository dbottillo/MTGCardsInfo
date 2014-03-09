package com.dbottillo.common;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBActivity extends ActionBarActivity {

    public void showLoadingInActionBar(){
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideLoadingFromActionBar(){
        setSupportProgressBarIndeterminateVisibility(false);
    }

    public SharedPreferences getSharedPreferences(){
        return getSharedPreferences(DBFragment.PREFS_NAME, 0);
    }
}
