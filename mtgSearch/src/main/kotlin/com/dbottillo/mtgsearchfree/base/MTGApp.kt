package com.dbottillo.mtgsearchfree.base

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.StrictMode
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.crashlytics.android.Crashlytics
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.communication.DataManager
import com.dbottillo.mtgsearchfree.component.AndroidComponent
import com.dbottillo.mtgsearchfree.component.DaggerAndroidComponent
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.modules.AndroidModule
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.fabric.sdk.android.Fabric
import java.util.*

class MTGApp : Application() {

    companion object {
        val INTENT_RELEASE_NOTE_PUSH = "Release push note"

        var cardsToDisplay: ArrayList<MTGCard>? = null

        var refWatcher: RefWatcher? = null

        val PREFS_NAME = "Filter"

        @JvmStatic lateinit var graph: AndroidComponent
    }

    override fun onCreate() {
        super.onCreate()

        graph = DaggerAndroidComponent.builder().androidModule(AndroidModule(this)).build()
        graph.inject(this)

        DataManager.with(this)
        Fabric.with(this, Crashlytics())
        Crashlytics.setString("git_sha", BuildConfig.GIT_SHA)
        refWatcher = LeakCanary.install(this)
        checkReleaseNote()

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build())
        }
    }

    protected fun checkReleaseNote() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, 0)
        val versionCode = sharedPreferences.getInt("VersionCode", -1)
        if (versionCode < BuildConfig.VERSION_CODE) {
            TrackingHelper.getInstance(applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "update")
            fireReleaseNotePush()
            sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply()
        }
    }

    private fun fireReleaseNotePush() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(INTENT_RELEASE_NOTE_PUSH, true)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)

        val b = NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_notification_generic)
                .setPriority(Notification.PRIORITY_LOW)
                .setContentTitle(getString(R.string.release_note_title_push, getString(R.string.app_name)))
                .setContentText(getText(R.string.release_note_text))
                .setColor(resources.getColor(R.color.color_primary))
                .setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            b.setCategory(Notification.CATEGORY_RECOMMENDATION)
        }

        b.setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.release_note_text)))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, b.build())
    }

}
