package com.dbottillo.mtgsearchfree.view.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.communication.events.BaseEvent
import com.dbottillo.mtgsearchfree.component.AppComponent
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import de.greenrobot.event.EventBus

abstract class BasicFragment : DialogFragment() {

    protected var dbActivity: AppCompatActivity? = null
    protected var isPortrait: Boolean = false
    protected var bus = EventBus.getDefault()

    companion object {
        val PREF_SHOW_IMAGE = "show_image"
        val PREF_SCREEN_ON = "screen_on"
        val PREF_TWO_HG_ENABLED = "two_hg"
        val PREF_SORT_WUBRG = "sort_wubrg"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.dbActivity = context as AppCompatActivity?
        val res = context!!.resources
        isPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setupComponent(MTGApp.Companion.graph);

        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = MTGApp.refWatcher
        refWatcher?.watch(this)
    }

    val sharedPreferences: SharedPreferences
        get() = dbActivity!!.getSharedPreferences(MTGApp.Companion.PREFS_NAME, 0)

    protected fun setActionBarTitle(title: String) {
        if (dbActivity!!.supportActionBar != null) {
            dbActivity!!.supportActionBar!!.title = title
        }
    }

    protected fun openPlayStore() {
        TrackingHelper.getInstance(dbActivity!!.applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_POPUP, TrackingHelper.UA_ACTION_OPEN, "play_store")
        val appPackageName = "com.dbottillo.mtgsearch"
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)))
        }

    }

    override fun onResume() {
        super.onResume()
        if (getPageTrack() != null) {
            TrackingHelper.getInstance(dbActivity!!.applicationContext).trackPage(getPageTrack())
        }
        bus.registerSticky(this)
    }

    abstract fun getPageTrack(): String?

    val app: MTGApp
        get() = dbActivity!!.application as MTGApp

    open fun onBackPressed(): Boolean {
        return false
    }

    fun onEvent(event: BaseEvent<Any>) {

    }

    fun getIsPortrait(): Boolean {
        return isPortrait;
    }

    abstract fun setupComponent(appComponent: AppComponent)

}
