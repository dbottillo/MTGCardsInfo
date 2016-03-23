package com.dbottillo.mtgsearchfree.base;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.communication.events.BaseEvent;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

import de.greenrobot.event.EventBus;

import static android.net.Uri.parse;

public abstract class DBActivity extends AppCompatActivity {

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MTGApp.PREFS_NAME, 0);
    }

    MTGApp app;
    protected Toolbar toolbar;
    protected EventBus bus = EventBus.getDefault();
    protected int sizeToolbar = 0;
    protected boolean isPortrait = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        app = (MTGApp) getApplication();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            sizeToolbar = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.registerSticky(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getPageTrack() != null) {
            TrackingHelper.getInstance(getApplicationContext()).trackPage(getPageTrack());
        }
    }

    public abstract String getPageTrack();

    protected MTGApp getApp() {
        return app;
    }



    protected void hideIme() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        }

        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
        }
    }

    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MaterialWrapper.setElevation(toolbar, getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
    }

    protected void setTitle(String name) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
        }
    }

    public void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    public void changeFragment(BasicFragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    public void openRateTheApp() {
        String packageName = getPackageName();
        if (BuildConfig.DEBUG) {
            packageName = "com.dbottillo.mtgsearchfree";
        }
        Uri uri = parse("market://details?id=" + packageName);
        Uri play = parse("http://play.google.com/store/apps/details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Intent goToPlay = new Intent(Intent.ACTION_VIEW, play);
            goToPlay.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(goToPlay);
        }
        TrackingHelper.getInstance(getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_RATE, "google");
    }


    public void onEvent(BaseEvent event) {

    }
}
