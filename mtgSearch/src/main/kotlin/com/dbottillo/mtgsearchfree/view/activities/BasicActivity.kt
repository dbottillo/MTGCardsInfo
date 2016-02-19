package com.dbottillo.mtgsearchfree.view.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.communication.events.BaseEvent
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment
import de.greenrobot.event.EventBus

abstract class BasicActivity : AppCompatActivity() {

    var app: MTGApp? = null
    var sizeToolbar = 0
    var bus: EventBus = EventBus.getDefault()
    var toolbar: Toolbar? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle);
        app = application as MTGApp?;

        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            sizeToolbar = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
        }
    }

    override fun onStart() {
        super.onStart()
        bus.register(this);
    }

    override fun onStop() {
        super.onStop()
        bus.unregister(this);
    }

    override fun onResume() {
        super.onResume()
        if (getPageTrack() != null) {
            TrackingHelper.getInstance(applicationContext).trackPage(getPageTrack())
        }
    }

    abstract fun getPageTrack(): String?

    fun onEvent(event: BaseEvent<Any>) {
    }

    fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        MaterialWrapper.setElevation(toolbar, resources.getDimensionPixelSize(R.dimen.toolbar_elevation).toFloat())
    }

    fun changeFragment(fragment: BasicFragment, tag: String, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
    }

    fun openRateTheApp() {
        var packageName = packageName
        if (BuildConfig.DEBUG) {
            packageName = "com.dbottillo.mtgsearchfree"
        }
        val uri = Uri.parse("market://details?id=" + packageName)
        val play = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            val goToPlay = Intent(Intent.ACTION_VIEW, play)
            goToPlay.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivity(goToPlay)
        }
        TrackingHelper.getInstance(applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_UI, TrackingHelper.UA_ACTION_RATE, "google")
    }
}
