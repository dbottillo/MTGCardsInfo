package com.dbottillo.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.dbottillo.dialog.AboutFragment;
import com.dbottillo.dialog.GoToPremiumFragment;
import com.dbottillo.dialog.PriceInfoFragment;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public abstract class DBActivity extends FragmentActivity {

    public enum DBDialog{
        ABOUT("about"),
        PRICE_INFO("price"),
        PREMIUM("premium");

        private String tag;

        DBDialog(String tag){
            this.tag = tag;
        }

        public String getTag(){
            return tag;
        }

        public DBFragment getFragment(){
            if (this == ABOUT) return new AboutFragment();
            if (this == PRICE_INFO) return new PriceInfoFragment();
            return new GoToPremiumFragment();
        }

    }

    public void showLoadingInActionBar(){
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideLoadingFromActionBar(){
        setProgressBarIndeterminateVisibility(false);
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

        if (app != null && getPageTrack() != null) {
            app.trackPage(getPageTrack());
        }
    }

    public abstract String getPageTrack();

    protected MTGApp getApp(){
        return app;
    }

    public void openDialog(DBDialog dbDialog){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dbDialog.getTag());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dbDialog.getFragment().show(ft, dbDialog.getTag());
    }
}
