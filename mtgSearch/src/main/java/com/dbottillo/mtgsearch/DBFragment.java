package com.dbottillo.mtgsearch;


import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBFragment extends Fragment {

    public static final String PREFS_NAME = "Filter";

    public static final String PREF_SHOW_IMAGE = "show_image";

    public SharedPreferences getSharedPreferences(){
        return getActivity().getSharedPreferences(PREFS_NAME, 0);
    }


    public void showRefresh(){
        DBActivity act = (DBActivity) getActivity();
        act.showLoadingInActionBar();
    }

    public void hideRefresh(){
        DBActivity act = (DBActivity) getActivity();
        act.hideLoadingFromActionBar();
    }

    protected void setActionBarTitle(String title) {
        DBActivity act = (DBActivity) getActivity();
        act.getSupportActionBar().setTitle(title);
    }

}
