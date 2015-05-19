package com.dbottillo.base;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MTGApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
        refWatcher = LeakCanary.install(this);
    }

    public static boolean isPremium() {
        return BuildConfig.FLAVOR.equalsIgnoreCase("paid");
    }

    public static RefWatcher getRefWatcher(Context context) {
        MTGApp application = (MTGApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
}
