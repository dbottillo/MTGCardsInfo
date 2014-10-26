package com.dbottillo.helper;

import android.content.Context;

import com.dbottillo.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class TrackingHelper {

    public static final String UA_CATEGORY_UI = "ui";
    public static final String UA_CATEGORY_POPUP = "popup";
    public static final String UA_CATEGORY_SET = "set";
    public static final String UA_CATEGORY_CARD = "card";
    public static final String UA_CATEGORY_SEARCH = "search";
    public static final String UA_CATEGORY_FILTER = "filter";
    public static final String UA_CATEGORY_FAVOURITE = "favourite";
    public static final String UA_CATEGORY_LIFE_COUNTER = "lifeCounter";
    public static final String UA_CATEGORY_ERROR = "error";

    public static final String UA_ACTION_CLICK = "click";
    public static final String UA_ACTION_TOGGLE = "toggle";
    public static final String UA_ACTION_SHARE = "share";
    public static final String UA_ACTION_SELECT = "select";
    public static final String UA_ACTION_OPEN = "open";
    public static final String UA_ACTION_CLOSE = "close";
    public static final String UA_ACTION_SAVED = "saved";
    public static final String UA_ACTION_UNSAVED = "unsaved";

    private static TrackingHelper instance;

    private static Tracker tracker;

    private TrackingHelper(Context context) {
        GoogleAnalytics.getInstance(context).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = GoogleAnalytics.getInstance(context).newTracker(context.getString(R.string.analytics));
        tracker.enableAdvertisingIdCollection(true);
    }

    public static void init(Context context) {
        instance = new TrackingHelper(context);
    }

    /*public static TrackingHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TrackingHelper(context);
        }
        return instance;
    }*/

    public static void trackPage(String page) {
        if (tracker != null) {
            tracker.setScreenName(page);
            tracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    public static void trackEvent(String category, String action) {
        TrackingHelper.trackEvent(category, action, "");
    }


    public static void trackEvent(String category, String action, String label) {
        if (tracker != null) {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }
    }
}
