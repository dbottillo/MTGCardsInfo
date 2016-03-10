package com.dbottillo.mtgsearchfree.tracking

import android.content.Context
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

object TrackingManager {

    var tracker: Tracker? = null

    fun init(context: Context) {
        val analytics = GoogleAnalytics.getInstance(context)
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        tracker = analytics.newTracker(R.xml.global_tracker)
        tracker?.enableAdvertisingIdCollection(true)
    }

    fun trackCard(gameSet: MTGSet?, position: Int) {
        trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SELECT, gameSet!!.name + " pos:" + position)
    }

    fun trackPage(page: String) {
        tracker?.setScreenName(page)
        tracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    fun trackEvent(category: String, action: String) {
        trackEvent(category, action, "")
    }

    fun trackEvent(category: String, action: String, label: String) {
        tracker?.send(HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build())
    }

    fun trackSet(gameSet: MTGSet?, mtgSet: MTGSet) {
        trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_SELECT, mtgSet.code)
    }

}