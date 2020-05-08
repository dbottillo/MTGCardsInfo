package com.dbottillo.mtgsearchfree.home

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.dbottillo.mtgsearchfree.Navigator
import com.dbottillo.mtgsearchfree.core.BuildConfig
import com.dbottillo.mtgsearchfree.core.R
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.toolbarereveal.ToolbarRevealScrollHelper
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import javax.inject.Inject

abstract class BaseHomeFragment : BasicFragment(), Toolbar.OnMenuItemClickListener {

    lateinit var toolbarRevealScrollHelper: ToolbarRevealScrollHelper

    @Inject lateinit var generalData: GeneralData
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarRevealScrollHelper = ToolbarRevealScrollHelper(this, getScrollViewId(),
            getToolbarId(), getToolbarTitleId(), heightToolbar)
    }

    abstract fun getScrollViewId(): Int
    abstract fun getToolbarId(): Int
    abstract fun getToolbarTitleId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarRevealScrollHelper.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.main_more)
        if (BuildConfig.DEBUG) {
            toolbar.inflateMenu(R.menu.main_debug)
        }
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        toolbarRevealScrollHelper.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        toolbarRevealScrollHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        toolbarRevealScrollHelper.onPause()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.more_rate -> dbActivity.openRateTheApp()
            R.id.more_about -> navigator.openAboutScreen(requireActivity())
            R.id.more_release_note -> navigator.openReleaseNoteScreen(requireActivity())
            R.id.more_settings -> navigator.openSettingsScreen(requireActivity())
            R.id.action_open_debug -> navigator.openDebugScreen(requireActivity())
        }
        return true
    }
}