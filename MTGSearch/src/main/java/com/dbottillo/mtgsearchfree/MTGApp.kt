package com.dbottillo.mtgsearchfree

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.dbottillo.mtgsearchfree.dagger.*
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.ui.HomeActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

open class MTGApp : Application() {

    lateinit var uiGraph: UiComponent

    @Inject
    lateinit var cardsPreferences: CardsPreferences

    override fun onCreate() {
        super.onCreate()

        LOG.d("============================================")
        LOG.d("            MTGApp created")
        LOG.d("============================================")

        val graph = DaggerAppComponent.builder()
                .androidModule(generateAndroidModule())
                .dataModule(generateDataModule())
                .build()
        graph.inject(this)

        uiGraph = DaggerUiComponent.builder()
                .appComponent(graph)
                .presentersModule(PresentersModule())
                .build()

        if (!isTesting()) {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                createNotificationChannel()
            }
            Fabric.with(this, Crashlytics())
            Crashlytics.setString("git_sha", BuildConfig.GIT_SHA)
            checkReleaseNote()

            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build())
            }

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
    }

    protected open fun isTesting(): Boolean{
        return false
    }

    protected open fun generateDataModule(): DataModule {
        return DataModule()
    }

    private fun generateAndroidModule(): AndroidModule {
        return AndroidModule(this)
    }

    private fun checkReleaseNote() {
        LOG.d()
        val versionCode = cardsPreferences.versionCode
        if (versionCode < BuildConfig.VERSION_CODE) {
            TrackingManager.trackReleaseNote()
            fireReleaseNotePush()
            cardsPreferences.saveSetPosition(0)
            cardsPreferences.saveVersionCode()
        }
    }

    private fun fireReleaseNotePush() {
        LOG.d()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(INTENT_RELEASE_NOTE_PUSH, true)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_notification_generic)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(getString(R.string.release_note_title_push, getString(R.string.app_name)))
                .setContentText(getText(R.string.release_note_text))
                .setColor(ContextCompat.getColor(this, R.color.color_primary))
                .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.release_note_text)))
                .setContentIntent(resultPendingIntent).apply {

            if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                setCategory(Notification.CATEGORY_RECOMMENDATION)
            }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, build())
        }
    }

    @RequiresApi(VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "MTG Cards Notifications"
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    companion object {
        val INTENT_RELEASE_NOTE_PUSH = "Release push note"
        val NOTIFICATION_CHANNEL_ID = "Base Channel"
        val TELEGRAM_LINK = "https://t.me/joinchat/B5gyzg14cbvCYiW7mtsWhQ"
    }

}
