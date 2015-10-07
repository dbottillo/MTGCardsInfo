package com.dbottillo.base;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.dbottillo.database.DB40Helper;
import com.dbottillo.resources.MTGCard;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MTGApp extends Application {

    private static ArrayList<MTGCard> cardsToDisplay;

    @Override
    public void onCreate() {
        super.onCreate();
        DB40Helper.init(this);
        Fabric.with(this, new Crashlytics());
        Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((MTGApp) context.getApplicationContext()).refWatcher;
    }

    private RefWatcher refWatcher;

    public static void setCardsToDisplay(ArrayList<MTGCard> cards) {
        cardsToDisplay = cards;
    }

    public static ArrayList<MTGCard> getCardsToDisplay() {
        return cardsToDisplay;
    }
}
