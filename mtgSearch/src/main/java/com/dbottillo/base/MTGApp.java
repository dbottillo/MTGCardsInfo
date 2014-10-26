package com.dbottillo.base;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.dbottillo.helper.TrackingHelper;

public class MTGApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        TrackingHelper.init(this);

    }

    public static boolean isPremium() {
        return BuildConfig.FLAVOR.equalsIgnoreCase("paid");
    }
}
