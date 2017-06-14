package com.dbottillo.mtgsearchfree.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.MTGApp
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment
import javax.inject.Inject

abstract class BasicActivity : AppCompatActivity() {

    protected var sizeToolbar = 0
    protected var toolbar: Toolbar? = null
    protected var isPortrait = false

    @Inject
    lateinit var generalData: GeneralData

    val mtgApp: MTGApp
        get() = application as MTGApp

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        LOG.d("============================================")

        mtgApp.uiGraph.inject(this)

        isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            sizeToolbar = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
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
        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).windowToken, 0)

        currentFocus?.clearFocus()
    }

    protected fun setupToolbar() {
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

        TrackingManager.trackOpenRateApp()
    }

    protected fun openDialog(tag: String, fragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        fragment.show(ft, tag)
    }

    private var permissionListener: PermissionUtil.PermissionListener? = null

    fun requestPermission(type: PermissionUtil.TYPE, listener: PermissionUtil.PermissionListener) {
        this.permissionListener = listener
        if (PermissionUtil.permissionGranted(this, type)) {
            listener.permissionGranted()
            return
        }
        PermissionUtil.requestPermission(this, type)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.isGranted(grantResults)) {
            permissionListener!!.permissionGranted()
        } else {
            permissionListener!!.permissionNotGranted()
        }
    }

    override fun getSystemService(name: String): Any {
        if ("Dagger" == name) {
            return mtgApp.uiGraph
        }
        return super.getSystemService(name)
    }

}
