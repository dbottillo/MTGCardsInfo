package com.dbottillo.base;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.util.MaterialWrapper;
import com.squareup.leakcanary.RefWatcher;

public abstract class DBFragment extends DialogFragment {

    public static final String PREFS_NAME = "Filter";
    public static final String PREF_SHOW_IMAGE = "show_image";
    public static final String PREF_SCREEN_ON = "screen_on";
    public static final String PREF_TWO_HG_ENABLED = "two_hg";
    public static final String PREF_SORT_WUBRG = "sort_wubrg";

    private DBActivity activity;
    protected boolean isPortrait;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (DBActivity) activity;
        Resources res = activity.getResources();
        isPortrait = res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MTGApp.getRefWatcher(activity);
        refWatcher.watch(this);
    }

    public SharedPreferences getSharedPreferences() {
        return activity.getSharedPreferences(PREFS_NAME, 0);
    }

    protected void setActionBarTitle(String title) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    protected void openPlayStore() {
        TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_POPUP, TrackingHelper.UA_ACTION_OPEN, "play_store");
        String appPackageName = "com.dbottillo.mtgsearch";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPageTrack() != null) {
            TrackingHelper.getInstance(activity).trackPage(getPageTrack());
        }
    }

    public abstract String getPageTrack();

    public MTGApp getApp() {
        return activity.getApp();
    }

    protected DBActivity getDBActivity() {
        return activity;
    }

    public boolean onBackPressed() {
        return false;
    }
}
