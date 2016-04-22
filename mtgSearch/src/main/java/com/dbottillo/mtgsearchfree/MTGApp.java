package com.dbottillo.mtgsearchfree;

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

import com.crashlytics.android.Crashlytics;
import com.dbottillo.mtgsearchfree.dagger.AndroidModule;
import com.dbottillo.mtgsearchfree.dagger.AppComponent;
import com.dbottillo.mtgsearchfree.dagger.DaggerAppComponent;
import com.dbottillo.mtgsearchfree.dagger.DaggerUiComponent;
import com.dbottillo.mtgsearchfree.dagger.UiComponent;
import com.dbottillo.mtgsearchfree.dagger.PresentersModule;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MTGApp extends Application {

    public static AppComponent graph;
    public static UiComponent uiGraph;
    public static ArrayList<MTGCard> cardsToDisplay = null;

    public static String INTENT_RELEASE_NOTE_PUSH = "Release push note";
    public static String PREFS_NAME = "Filter";
    private RefWatcher refWatcher;
    protected boolean isUnitTesting = false;

    @Override
    public void onCreate() {
        super.onCreate();

        LOG.d("============================================");
        LOG.d("            MTGApp created");
        LOG.d("============================================");

        graph = DaggerAppComponent.builder().androidModule(generateAndroidModule()).build();
        graph.inject(this);

        uiGraph = DaggerUiComponent.builder()
                .appComponent(graph)
                .presentersModule(new PresentersModule()).build();

        if (!isUnitTesting) {
            TrackingManager.init(getApplicationContext());
            Fabric.with(this, new Crashlytics());
            Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
            refWatcher = LeakCanary.install(this);
            checkReleaseNote();

            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());
            }
        }
    }

    protected AndroidModule generateAndroidModule(){
        return new AndroidModule(this);
    }

    protected void checkReleaseNote() {
        LOG.d();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        int versionCode = sharedPreferences.getInt("VersionCode", -1);
        if (versionCode < BuildConfig.VERSION_CODE) {
            TrackingManager.trackReleaseNote();
            fireReleaseNotePush();
            sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply();
        }
    }

    private void fireReleaseNotePush() {
        LOG.d();
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

    public static RefWatcher getRefWatcher(Context context) {
        LOG.d();
        MTGApp application = (MTGApp) context.getApplicationContext();
        return application.refWatcher;
    }
}
