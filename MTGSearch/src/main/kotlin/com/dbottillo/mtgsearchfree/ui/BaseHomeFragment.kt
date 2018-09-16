package com.dbottillo.mtgsearchfree.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.helper.AddFavouritesAsyncTask
import com.dbottillo.mtgsearchfree.model.helper.CreateDecksAsyncTask
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.toolbarereveal.ToolbarRevealScrollHelper
import com.dbottillo.mtgsearchfree.ui.about.AboutActivity
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.copyDbFromSdCard
import javax.inject.Inject

abstract class BaseHomeFragment : BasicFragment(), Toolbar.OnMenuItemClickListener {

    lateinit var toolbarRevealScrollHelper: ToolbarRevealScrollHelper

    @Inject
    lateinit var generalData : GeneralData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarRevealScrollHelper = ToolbarRevealScrollHelper(this, getScrollViewId(),
                R.color.white, heightToolbar, true)

    }

    abstract fun getScrollViewId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarRevealScrollHelper.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.main_more)
        if (BuildConfig.DEBUG) {
            toolbar.inflateMenu(R.menu.main_debug)
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
            R.id.more_about -> startActivity(Intent(activity, AboutActivity::class.java))
            R.id.more_release_note -> startActivity(Intent(activity, ReleaseNoteActivity::class.java))
            R.id.action_create_db -> (activity as HomeActivity).recreateDb()
            R.id.action_fill_decks -> CreateDecksAsyncTask(app.applicationContext).execute()
            R.id.action_create_fav -> AddFavouritesAsyncTask(app.applicationContext).execute()
            R.id.action_crash -> throw RuntimeException("This is a crash")
            R.id.action_send_db -> (activity as HomeActivity).copyDBToSdCard()
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