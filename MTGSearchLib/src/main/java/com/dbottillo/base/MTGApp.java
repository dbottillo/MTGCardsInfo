package com.dbottillo.base;

import android.app.Application;
import android.util.Log;

import com.dbottillo.mtgsearch.BuildConfig;
import com.dbottillo.mtgsearch.R;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import java.util.HashMap;

/**
 * Created by danielebottillo on 09/03/2014.
 */
public class MTGApp extends Application {

    Tracker tracker;

    @Override
    public void onCreate(){
        super.onCreate();

        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

        tracker = GoogleAnalytics.getInstance(this).getTracker(getString(R.string.analytics));
    }

    public void trackPage(String page){
        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.HIT_TYPE, "appview");
        hitParameters.put(Fields.SCREEN_NAME, page);
        if (!BuildConfig.DEBUG) {
            tracker.send(hitParameters);
        }else{
            Log.e("MTG", "view no it's debug");
        }
    }

    public static final String UA_CATEGORY_UI = "ui";
    public static final String UA_CATEGORY_SEARCH = "search";
    public static final String UA_ACTION_CLICK = "click";
    public static final String UA_ACTION_TOGGLE = "toggle";
    public static final String UA_ACTION_OPEN = "open";


    public void trackEvent(String category, String action, String label){
        if (!BuildConfig.DEBUG) {
            tracker.send(MapBuilder.createEvent(category, action, label, null).build());
        }
    }
}
