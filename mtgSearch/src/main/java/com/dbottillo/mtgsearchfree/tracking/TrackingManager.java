package com.dbottillo.mtgsearchfree.tracking;

import android.content.Context;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class TrackingManager {
    static Tracker tracker = null;

    public static void init(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.enableAdvertisingIdCollection(true);
    }

    public static void trackCard(MTGSet gameSet, int position) {
        trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet.getName() + " pos:" + position);
    }

    public static void trackPage(String page) {
        if (page != null) {
            tracker.setScreenName(page);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void trackEvent(String category, String action) {
        trackEvent(category, action, "");
    }

    public static void trackEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    public static void trackSet(MTGSet gameSet, MTGSet mtgSet) {
        trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_SELECT, mtgSet.getCode());
    }

    public static void trackShareApp() {
        trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_SHARE, "app");
    }

    public static void trackAboutLibrary(String libraryLink) {
        trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_EXTERNAL_LINK, libraryLink);
    }

    public static void trackFeedback() {
        trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_OPEN, "feedback");
    }

    public static void trackPriceError(String url) {
        trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "price", url);
    }

    public static void trackImageError(String image) {
        trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "image", image);
    }

    public static void trackShareCard(MTGCard card) {
        if (card != null) {
            trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SHARE, card.getName());
        }
    }
}
