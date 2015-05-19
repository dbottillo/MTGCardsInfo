package com.dbottillo.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.dialog.AboutFragment;
import com.dbottillo.dialog.GoToPremiumFragment;
import com.dbottillo.dialog.PriceInfoFragment;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.util.MaterialWrapper;

public abstract class DBActivity extends AppCompatActivity {

    public enum DBDialog {
        ABOUT("about"),
        PRICE_INFO("price"),
        PREMIUM("premium");

        private String tag;

        DBDialog(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public DBFragment getFragment() {
            if (this == ABOUT) return new AboutFragment();
            if (this == PRICE_INFO) return new PriceInfoFragment();
            return new GoToPremiumFragment();
        }

    }

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

    public void openDialog(DBDialog dbDialog) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dbDialog.getTag());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dbDialog.getFragment().show(ft, dbDialog.getTag());
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_dark);
        setSupportActionBar(toolbar);
        MaterialWrapper.setElevation(toolbar, getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
    }

    public void setToolbarColor(int color){
        toolbar.setBackgroundColor(color);
    }

}
