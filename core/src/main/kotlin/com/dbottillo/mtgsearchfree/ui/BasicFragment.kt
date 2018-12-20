package com.dbottillo.mtgsearchfree.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager

abstract class BasicFragment : DialogFragment() {

    lateinit var dbActivity: BasicActivity
    private var isPortrait = false
    lateinit var toolbar: Toolbar
    var toolbarTitle: TextView? = null
    protected var heightToolbar: Int = 0

    protected val app: Application
        get() = dbActivity.mtgApp

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LOG.d()

        this.dbActivity = context as BasicActivity
        isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (heightToolbar <= 0) {
            val styledAttributes = dbActivity.theme.obtainStyledAttributes(intArrayOf(android.support.v7.appcompat.R.attr.actionBarSize))
            heightToolbar = styledAttributes.getDimension(0, 0f).toInt()
            styledAttributes.recycle()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOG.d()

        setHasOptionsMenu(true)

        TrackingManager.logOnCreate("${javaClass.name} ${hashCode()}")
    }

    protected fun setActionBarTitle(title: String) {
        dbActivity.supportActionBar?.title = title
    }

    override fun onResume() {
        super.onResume()
        LOG.d()
        TrackingManager.trackPage(getPageTrack())
    }

    abstract fun getPageTrack(): String

    abstract fun getTitle(): String

    fun onBackPressed(): Boolean {
        return false
    }

    fun setupToolbar(rootView: View, toolbarId: Int, toolbarTitleId: Int) {
        toolbar = rootView.findViewById<View>(toolbarId) as Toolbar
        toolbar.title = ""
        toolbarTitle = toolbar.findViewById<View>(toolbarTitleId) as TextView
        setTitle(getTitle())
    }

    fun setTitle(title: String?) {
        toolbarTitle?.text = title
    }

    companion object {
        const val PREF_SHOW_IMAGE = "show_image"
        const val PREF_SCREEN_ON = "screen_on"
        const val PREF_TWO_HG_ENABLED = "two_hg"
        const val PREF_SORT_WUBRG = "sort_wubrg"
    }
}