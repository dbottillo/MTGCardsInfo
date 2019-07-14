package com.dbottillo.mtgsearchfree.home

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dbottillo.mtgsearchfree.Navigator
import com.dbottillo.mtgsearchfree.core.BuildConfig
import com.dbottillo.mtgsearchfree.core.R
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.toolbarereveal.ToolbarRevealScrollHelper
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.copyDbFromSdCard
import javax.inject.Inject

abstract class BaseHomeFragment : BasicFragment(), Toolbar.OnMenuItemClickListener {

    lateinit var toolbarRevealScrollHelper: ToolbarRevealScrollHelper

    @Inject lateinit var generalData: GeneralData
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarRevealScrollHelper = ToolbarRevealScrollHelper(this, getScrollViewId(),
            getToolbarId(), getToolbarTitleId(), R.color.white, heightToolbar, true)
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
        if (generalData.isDebugEnabled()) {
            toolbar.inflateMenu(R.menu.main_user_debug)
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
            R.id.more_about -> navigator.openAboutScreen(activity!!)
            R.id.more_release_note -> navigator.openReleaseNoteScreen(activity!!)
            R.id.action_create_db -> (activity as BasicActivity).recreateDb()
            R.id.action_fill_decks -> navigator.createDecks(app.applicationContext)
            R.id.action_create_fav -> navigator.createFavourites(app.applicationContext)
            R.id.action_crash -> throw RuntimeException("This is a crash")
            R.id.action_send_db -> (activity as BasicActivity).copyDBToSdCard()
            R.id.action_copy_db -> {
                val copied = app.applicationContext.copyDbFromSdCard(CardsInfoDbHelper.DATABASE_NAME)
                Toast.makeText(app.applicationContext, if (copied) "database copied" else "database not copied", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    protected fun setupHomeActivityScroll(viewRecycle: RecyclerView) {
        if (activity is HomeActivity) {
            viewRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        (activity as HomeActivity).scrollingUp()
                    } else {
                        (activity as HomeActivity).scrollingDown()
                    }
                }
            })
        }
    }
}