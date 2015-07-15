package com.dbottillo.base;


import android.content.Intent;
import android.content.SharedPreferences;
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

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MTGApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    public SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    protected void setActionBarTitle(String title) {
        DBActivity act = (DBActivity) getActivity();
        act.getSupportActionBar().setTitle(title);
    }

    protected void openPlayStore() {
        TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_POPUP, TrackingHelper.UA_ACTION_OPEN, "play_store");
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
            TrackingHelper.getInstance(getActivity()).trackPage(getPageTrack());
        }
    }

    protected void setupToolbar(int title) {
        if (getView() != null) {
            setupToolbar(getView(), title);
        }
    }

    protected void setupToolbar(View view, int title) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "bla bla", Toast.LENGTH_SHORT).show();
            }
        });
        MaterialWrapper.setElevation(toolbar, getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
        AppCompatActivity appCompactActivity = (AppCompatActivity) getActivity();
        appCompactActivity.setSupportActionBar(toolbar);
        if (appCompactActivity.getSupportActionBar() != null) {
            appCompactActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            appCompactActivity.getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public abstract String getPageTrack();

    public MTGApp getApp() {
        if (getActivity() != null) {
            return ((DBActivity) getActivity()).getApp();
        }
        return null;
    }


}
