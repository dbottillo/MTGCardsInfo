package com.dbottillo.mtgsearchfree;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.crashlytics.android.Crashlytics;
import com.dbottillo.mtgsearchfree.dagger.AndroidModule;
import com.dbottillo.mtgsearchfree.dagger.AppComponent;
import com.dbottillo.mtgsearchfree.dagger.DaggerAppComponent;
import com.dbottillo.mtgsearchfree.dagger.DaggerUiComponent;
import com.dbottillo.mtgsearchfree.dagger.DataModule;
import com.dbottillo.mtgsearchfree.dagger.PresentersModule;
import com.dbottillo.mtgsearchfree.dagger.UiComponent;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.ui.HomeActivity;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

public class MTGApp extends Application {

    private UiComponent uiGraph;
    public static final String INTENT_RELEASE_NOTE_PUSH = "Release push note";
    boolean isUnitTesting = false;

    @Inject
    CardsPreferences cardsPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        LOG.d("============================================");
        LOG.d("            MTGApp created");
        LOG.d("============================================");

        AppComponent graph = DaggerAppComponent.builder()
                .androidModule(generateAndroidModule())
                .dataModule(generateDataModule())
                .build();
        graph.inject(this);

        uiGraph = DaggerUiComponent.builder()
                .appComponent(graph)
                .presentersModule(new PresentersModule())
                .build();

        if (!isUnitTesting) {
            Fabric.with(this, new Crashlytics());
            Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
            checkReleaseNote();

            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());
            }

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }
    }

    protected DataModule generateDataModule() {
        return new DataModule();
    }

    protected AndroidModule generateAndroidModule() {
        return new AndroidModule(this);
    }

    private void checkReleaseNote() {
        LOG.d();
        int versionCode = cardsPreferences.getVersionCode();
        if (versionCode < BuildConfig.VERSION_CODE) {
            TrackingManager.trackReleaseNote();
            fireReleaseNotePush();
            cardsPreferences.saveSetPosition(0);
            cardsPreferences.saveVersionCode();
        }
    }

    private void fireReleaseNotePush() {
        LOG.d();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(INTENT_RELEASE_NOTE_PUSH, true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
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


        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            b.setCategory(Notification.CATEGORY_RECOMMENDATION);
        }

        b.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.release_note_text)));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }

    public static boolean isActivityTransitionAvailable() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }

    public UiComponent getUiGraph() {
        return uiGraph;
    }

}
