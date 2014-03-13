package com.dbottillo.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public abstract class DBActivity extends ActionBarActivity {

    public void showLoadingInActionBar(){
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideLoadingFromActionBar(){
        setSupportProgressBarIndeterminateVisibility(false);
    }

    public SharedPreferences getSharedPreferences(){
        return getSharedPreferences(DBFragment.PREFS_NAME, 0);
    }

    MTGApp app;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        app = (MTGApp) getApplication();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (app != null) {
            app.trackPage(getPageTrack());
        }
    }

    public abstract String getPageTrack();

    protected MTGApp getApp(){
        return app;
    }
}
