package com.dbottillo.mtgsearchfree.view.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.LeakCanaryUtil;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity;

public abstract class BasicFragment extends DialogFragment {

    protected BasicActivity dbActivity;
    protected boolean isPortrait = false;
    protected MTGApp app;

    public static final String PREF_SHOW_IMAGE = "show_image";
    public static final String PREF_SCREEN_ON = "screen_on";
    public static final String PREF_TWO_HG_ENABLED = "two_hg";
    public static final String PREF_SORT_WUBRG = "sort_wubrg";


    public void onAttach(Context context) {
        super.onAttach(context);
        LOG.d();

        this.dbActivity = (BasicActivity) context;
        app = (MTGApp) dbActivity.getApplication();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    protected MTGApp getMTGApp() {
        return dbActivity.getMTGApp();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG.d();

        setHasOptionsMenu(true);
    }


    public void onDestroy() {
        super.onDestroy();
        LeakCanaryUtil.watchFragment(this);
    }

    void setActionBarTitle(String title) {
        if (dbActivity.getSupportActionBar() != null) {
            dbActivity.getSupportActionBar().setTitle(title);
        }
    }

    public void onResume() {
        super.onResume();
        LOG.d();
        TrackingManager.trackPage(getPageTrack());
    }

    public abstract String getPageTrack();


    public boolean onBackPressed() {
        return false;
    }

}

