package com.dbottillo.base;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.util.MaterialWrapper;

import static android.net.Uri.parse;

public abstract class DBActivity extends AppCompatActivity {

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(DBFragment.PREFS_NAME, 0);
    }

    MTGApp app;
    protected Toolbar toolbar;
    protected boolean onSaveInstanceStateCalled = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        onSaveInstanceStateCalled = false;
        app = (MTGApp) getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getPageTrack() != null) {
            TrackingHelper.getInstance(this).trackPage(getPageTrack());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        onSaveInstanceStateCalled = true;
        super.onSaveInstanceState(outState);
    }

    public abstract String getPageTrack();

    protected MTGApp getApp() {
        return app;
    }

    public void openDialog(String tag, DialogFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        fragment.show(ft, tag);
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

    public void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    public void changeFragment(DBFragment fragment, String tag, boolean addToBackStack) {
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
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_RATE, "google");
    }
}
