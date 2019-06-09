package com.dbottillo.mtgsearchfree.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.dbottillo.mtgsearchfree.Navigator
import com.dbottillo.mtgsearchfree.core.BuildConfig
import com.dbottillo.mtgsearchfree.core.R
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionAvailable
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.request
import javax.inject.Inject

abstract class BasicActivity : AppCompatActivity() {

    protected var sizeToolbar = 0
    protected lateinit var toolbar: Toolbar
    protected var isPortrait = false

    @Inject lateinit var generalData: GeneralData
    @Inject lateinit var navigator: Navigator

    val mtgApp: Application
        get() = application

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        LOG.d("============================================")

        isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            sizeToolbar = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }

        TrackingManager.logOnCreate("${javaClass.name} ${hashCode()}")
    }

    public override fun onResume() {
        super.onResume()
        LOG.d()
        getPageTrack()?.let {
            TrackingManager.trackPage(it)
        }
    }

    abstract fun getPageTrack(): String?

    protected fun hideIme() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)

        currentFocus?.clearFocus()
    }

    protected fun setupToolbar(toolbarId: Int) {
        toolbar = findViewById(toolbarId)
        setSupportActionBar(toolbar)
        toolbar.elevation = resources.getDimensionPixelSize(R.dimen.toolbar_elevation).toFloat()
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
        } catch (e: Throwable) {
            val goToPlay = Intent(Intent.ACTION_VIEW, play)
            goToPlay.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivity(goToPlay)
        }

        TrackingManager.trackOpenRateApp()
    }

    protected fun openDialog(tag: String, fragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.add(fragment, tag)
        ft.commitNowAllowingStateLoss()
    }

    private var permissionListener: PermissionUtil.PermissionListener? = null

    fun requestPermission(permission: PermissionAvailable, listener: PermissionUtil.PermissionListener) {
        this.permissionListener = listener
        if (PermissionUtil.permissionGranted(this, permission)) {
            listener.permissionGranted()
            return
        }
        this.request(permission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.isGranted(grantResults)) {
            permissionListener?.permissionGranted()
        } else {
            permissionListener?.permissionNotGranted()
        }
    }
}
