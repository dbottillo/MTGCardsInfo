package com.dbottillo.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.BuildConfig;
import com.dbottillo.communication.DataManager;
import com.dbottillo.persistence.MigrationPreferences;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.saved.MigrationService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MTGApp extends Application {

    private static ArrayList<MTGCard> cardsToDisplay;

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.with(this);
        Fabric.with(this, new Crashlytics());
        Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
        refWatcher = LeakCanary.install(this);
        migrateFavourites();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
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

    private void migrateFavourites() {
        MigrationPreferences migrationPreferences = new MigrationPreferences(this);
        if (migrationPreferences.migrationNotStarted()) {
            migrationPreferences.setStarted();
            Intent intent = new Intent(this, MigrationService.class);
            startService(intent);
        }
    }
}
