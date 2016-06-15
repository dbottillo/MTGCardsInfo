package com.dbottillo.mtgsearchfree.util;

import android.content.Context;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public final class TrackingManager {

    private TrackingManager() {
    }

    private static final String UA_CATEGORY_UI = "ui";
    private static final String UA_CATEGORY_POPUP = "popup";
    private static final String UA_CATEGORY_SET = "set";
    private static final String UA_CATEGORY_CARD = "card";
    private static final String UA_CATEGORY_SEARCH = "search";
    private static final String UA_CATEGORY_FILTER = "filter";
    private static final String UA_CATEGORY_FAVOURITE = "favourite";
    private static final String UA_CATEGORY_DECK = "deck";
    private static final String UA_CATEGORY_LIFE_COUNTER = "lifeCounter";
    private static final String UA_CATEGORY_ERROR = "error";9
    private static final String UA_CATEGORY_APP_WIDGET = "appWidget";
    private static final String UA_CATEGORY_RELEASE_NOTE = "releaseNote";

    private static final String UA_ACTION_TOGGLE = "toggle";
    private static final String UA_ACTION_SHARE = "share";
    private static final String UA_ACTION_SELECT = "select";
    private static final String UA_ACTION_OPEN = "open";
    private static final String UA_ACTION_CLOSE = "close";
    private static final String UA_ACTION_SAVE = "saved";
    private static final String UA_ACTION_ADD_CARD = "addCard";
    private static final String UA_ACTION_UNSAVED = "unsaved";
    private static final String UA_ACTION_LUCKY = "lucky";
    private static final String UA_ACTION_RATE = "rate";
    private static final String UA_ACTION_DELETE = "delete";
    private static final String UA_ACTION_EXTERNAL_LINK = "externalLink";
    private static final String UA_ACTION_ONE_MORE = "oneMore";
    private static final String UA_ACTION_REMOVE_ONE = "removeOne";
    private static final String UA_ACTION_REMOVE_ALL = "removeALL";
    private static final String UA_ACTION_EXPORT = "export";

    static Tracker tracker = null;

    public static void init(Context context) {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }
    }

    public static void trackCard(MTGSet gameSet, int position) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_SELECT, gameSet.getName() + " pos:" + position);
    }

    public static void trackPage(String page) {
        if (page != null) {
            tracker.setScreenName(page);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    private static void trackEvent(String category, String action) {
        trackEvent(category, action, "");
    }

    private static void trackEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    public static void trackSet(MTGSet gameSet, MTGSet mtgSet) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_SELECT, mtgSet.getCode());
    }

    public static void trackShareApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_SHARE, "app");
    }

    public static void trackAboutLibrary(String libraryLink) {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_EXTERNAL_LINK, libraryLink);
    }

    public static void trackPriceError(String url) {
        trackEvent(UA_CATEGORY_ERROR, "price", url);
    }

    public static void trackImageError(String image) {
        trackEvent(UA_CATEGORY_ERROR, "image", image);
    }

    public static void trackShareCard(MTGCard card) {
        if (card != null) {
            trackEvent(UA_CATEGORY_CARD, UA_ACTION_SHARE, card.getName());
        }
    }

    public static void trackReleaseNote() {
        trackEvent(UA_CATEGORY_RELEASE_NOTE, UA_ACTION_OPEN, "update");
    }

    public static void trackSortCard(int which) {
        trackEvent(UA_CATEGORY_SET, UA_ACTION_TOGGLE, which == 1 ? "wubrg" : "alphabetically");
    }

    public static void trackDatabaseExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_EXPORT);
    }

    public static void trackDatabaseExportError(String error) {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] " + error);
    }

    public static void trackOpenRateApp() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_RATE, "google");
    }

    public static void trackEditDeck() {
        trackEvent(UA_CATEGORY_DECK, "editName");
    }

    public static void trackDeckExport() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SHARE);
    }

    public static void trackDeckExportError() {
        trackEvent(UA_CATEGORY_ERROR, UA_ACTION_EXPORT, "[deck] impossible to create folder");
    }

    public static void trackAddCardToDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ONE_MORE);
    }

    public static void trackAddCardToDeck(String quantity) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_ADD_CARD, quantity);
    }

    public static void trackRemoveCardFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ONE);
    }

    public static void trackRemoveAllCardsFromDeck() {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_REMOVE_ALL);
    }

    public static void trackSearch(SearchParams searchParams) {
        trackEvent(UA_CATEGORY_SEARCH, "done", searchParams.toString());
    }

    public static void trackOpenFeedback() {
        trackEvent(UA_CATEGORY_UI, UA_ACTION_OPEN, "feedback");
    }

    public static void trackNewDeck(String deck) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_SAVE, deck);
    }

    public static void trackDeleteDeck(String name) {
        trackEvent(UA_CATEGORY_DECK, UA_ACTION_DELETE, name);
    }

    public static void trackAddPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "addPlayer");
    }

    public static void trackResetLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "resetLifeCounter");
    }

    public static void trackLunchDice() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "launchDice");
    }

    public static void trackChangePoisonSetting() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonSetting");
    }

    public static void trackHGLifeCounter() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "two_hg");
    }

    public static void trackOpenCard(int position) {
        trackEvent(UA_CATEGORY_CARD, UA_ACTION_OPEN, "saved pos:" + position);
    }

    public static void trackSearchError(String message) {
        trackEvent(UA_CATEGORY_ERROR, "saved-main", message);
    }

    public static void trackDeleteWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "deleted");
    }

    public static void trackAddWidget() {
        trackEvent(UA_CATEGORY_APP_WIDGET, "enabled");
    }

    public static void trackEditPlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "editPlayer");
    }

    public static void trackLifeCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "lifeCountChanged");
    }

    public static void trackPoisonCountChanged() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "poisonCountChange");
    }

    public static void trackScreenOn() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "screenOn");
    }

    public static void trackRemovePlayer() {
        trackEvent(UA_CATEGORY_LIFE_COUNTER, "removePlayer");
    }
}
