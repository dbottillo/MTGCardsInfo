package com.dbottillo.mtgsearchfree.base;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.communication.DataManager;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MTGApp extends Application {

    public static final String INTENT_RELEASE_NOTE_PUSH = "Release push note";

    private static ArrayList<MTGCard> cardsToDisplay;

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.with(this);
        Fabric.with(this, new Crashlytics());
        Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
        refWatcher = LeakCanary.install(this);
        checkReleaseNote();

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


    protected void checkReleaseNote() {
        SharedPreferences sharedPreferences = getSharedPreferences(DBFragment.PREFS_NAME, 0);
        int versionCode = sharedPreferences.getInt("VersionCode", -1);
        if (versionCode < BuildConfig.VERSION_CODE) {
            TrackingHelper.getInstance(getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "update");
            fireReleaseNotePush();
            sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply();
        }
    }

    private void fireReleaseNotePush() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_RELEASE_NOTE_PUSH, true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_notification_generic)
                .setPriority(Notification.PRIORITY_LOW)
                .setContentTitle(getString(R.string.release_note_title_push, getString(R.string.app_name)))
                .setContentText(getText(R.string.release_note_text))
                .setColor(getResources().getColor(R.color.color_primary))
                .setContentIntent(resultPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            b.setCategory(Notification.CATEGORY_RECOMMENDATION);
        }

        b.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.release_note_text)));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }
}
