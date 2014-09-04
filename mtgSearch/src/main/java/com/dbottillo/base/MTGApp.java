package com.dbottillo.base;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.dbottillo.R;
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
        Crashlytics.start(this);

        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = GoogleAnalytics.getInstance(this).getTracker(getString(R.string.analytics));
    }

    public void trackPage(String page){
        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.HIT_TYPE, "appview");
        hitParameters.put(Fields.SCREEN_NAME, page);
        tracker.send(hitParameters);
    }

    public static final String UA_CATEGORY_UI = "ui";
    public static final String UA_CATEGORY_SEARCH = "search";
    public static final String UA_CATEGORY_FAVOURITE = "favourite";
    public static final String UA_ACTION_CLICK = "click";
    public static final String UA_ACTION_TOGGLE = "toggle";
    public static final String UA_ACTION_OPEN = "open";
    public static final String UA_ACTION_SAVED = "saved";
    public static final String UA_ACTION_UNSAVED = "unsaved";
    public static final String UA_ACTION_LIFE_COUNTER = "lifeCounter";


    public void trackEvent(String category, String action, String label){
        tracker.send(MapBuilder.createEvent(category, action, label, null).build());
    }

    public static boolean isPremium(){
        return BuildConfig.FLAVOR.equalsIgnoreCase("paid");
    }
}
