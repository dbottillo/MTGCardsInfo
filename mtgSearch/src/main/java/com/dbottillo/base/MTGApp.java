package com.dbottillo.base;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;

public class MTGApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
    }

    public static boolean isPremium() {
        return BuildConfig.FLAVOR.equalsIgnoreCase("paid");
    }
}
