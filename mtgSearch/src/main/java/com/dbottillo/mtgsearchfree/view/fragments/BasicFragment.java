package com.dbottillo.mtgsearchfree.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.squareup.leakcanary.RefWatcher;

public abstract class BasicFragment extends DialogFragment {

    protected AppCompatActivity dbActivity;
    protected boolean isPortrait = false;
    protected MTGApp app;
    protected SharedPreferences sharedPreferences;

    public static final String PREF_SHOW_IMAGE = "show_image";
    public static final String PREF_SCREEN_ON = "screen_on";
    public static final String PREF_TWO_HG_ENABLED = "two_hg";
    public static final String PREF_SORT_WUBRG = "sort_wubrg";


    public void onAttach(Context context) {
        super.onAttach(context);

        this.dbActivity = (AppCompatActivity) context;
        app = (MTGApp) dbActivity.getApplication();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        sharedPreferences = dbActivity.getSharedPreferences(MTGApp.PREFS_NAME, 0);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MTGApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    protected void setActionBarTitle(String title) {
        if (dbActivity.getSupportActionBar() != null) {
            dbActivity.getSupportActionBar().setTitle(title);
        }
    }

    public void onResume() {
        super.onResume();
        TrackingManager.trackPage(getPageTrack());
    }

    public abstract String getPageTrack();


    public boolean onBackPressed() {
        return false;
    }

    public boolean getIsPortrait() {
        return isPortrait;
    }

}

