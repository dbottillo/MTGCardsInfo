package com.dbottillo.base;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class MTGApp extends Application {

    Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = GoogleAnalytics.getInstance(this).newTracker(getString(R.string.analytics));
        tracker.enableAdvertisingIdCollection(true);
    }

    public void trackPage(String page) {
        tracker.setScreenName(page);
        tracker.send(new HitBuilders.AppViewBuilder().build());
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


    public void trackEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static boolean isPremium() {
        return BuildConfig.FLAVOR.equalsIgnoreCase("paid");
    }
}
