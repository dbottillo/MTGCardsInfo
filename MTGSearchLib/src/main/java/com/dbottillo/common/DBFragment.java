package com.dbottillo.common;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBFragment extends DialogFragment {

    public static final String PREFS_NAME = "Filter";

    public static final String PREF_SHOW_IMAGE = "show_image";

    public SharedPreferences getSharedPreferences(){
        return getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    protected void setActionBarTitle(String title) {
        DBActivity act = (DBActivity) getActivity();
        act.getSupportActionBar().setTitle(title);
    }

    protected void openPlayStore(){
        String appPackageName = "com.dbottillo.mtgsearch";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}
