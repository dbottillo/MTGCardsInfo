package com.dbottillo.mtgsearchfree.ui

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.toolbarereveal.ToolbarRevealScrollHelper
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment

abstract class BaseHomeFragment : BasicFragment(), Toolbar.OnMenuItemClickListener {

    lateinit var toolbarRevealScrollHelper: ToolbarRevealScrollHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarRevealScrollHelper = ToolbarRevealScrollHelper(this, getScrollViewId(),
                R.color.white, heightToolbar, true)

    }

    abstract fun getScrollViewId(): Int

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarRevealScrollHelper.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.main_more)
        toolbar.setOnMenuItemClickListener(this)

    }

    override fun onSaveInstanceState(outState: Bundle?) {
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
        if (item?.itemId == R.id.more_rate){
            dbActivity.openRateTheApp()
            return true
        }
        if (item?.itemId == R.id.more_about) {
            //startActivity(Intent(activity, CardLuckyActivity::class.java))
            return true
        }
        if (item?.itemId == R.id.more_beta) {
            //startActivity(Intent(activity, CardLuckyActivity::class.java))
            return true
        }
        if (item?.itemId == R.id.more_release_note) {
            //startActivity(Intent(activity, CardLuckyActivity::class.java))
            return true
        }
        return true
    }

    protected fun setupHomeActivityScroll(recyclerView: RecyclerView) {
        if (activity is HomeActivity) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
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