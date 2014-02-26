package com.dbottillo.mtgsearch;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBActivity extends ActionBarActivity {

    public void showLoadingInActionBar(){
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideLoadingFromActionBar(){
        setSupportProgressBarIndeterminateVisibility(false);
    }
}
