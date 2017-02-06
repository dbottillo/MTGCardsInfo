package com.dbottillo.mtgsearchfree.ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.helper.AddFavouritesAsyncTask
import com.dbottillo.mtgsearchfree.model.helper.CreateDecksAsyncTask
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import com.dbottillo.mtgsearchfree.view.fragments.*
import com.dbottillo.mtgsearchfree.view.helpers.NavDrawerHelper

abstract class BaseDrawerActivity: BasicActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var navDrawerHelper: NavDrawerHelper? = null

    fun setupNavdrawer(toolbar: Toolbar? = null) {
        navDrawerHelper = NavDrawerHelper(this, findViewById(R.id.navigation_view) as NavigationView, null, this, generalData)
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navDrawerHelper?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navDrawerHelper?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*if (navDrawerHelper!!.onOptionsItemSelected(item)) {
            return true
        }*/
        if (item.itemId == android.R.id.home) {
            navDrawerHelper?.openDrawer()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as BasicFragment
        if (menuItem.itemId == R.id.drawer_home && currentFragment !is MainFragment) {
            changeFragment(MainFragment(), "main", false)

        } else if (menuItem.itemId == R.id.drawer_saved && currentFragment !is SavedFragment) {
            changeFragment(SavedFragment(), "saved_fragment", true)


        } else if (menuItem.itemId == R.id.drawer_life_counter && currentFragment !is LifeCounterFragment) {
            changeFragment(LifeCounterFragment.newInstance(), "life_counter", true)


        } else if (menuItem.itemId == R.id.drawer_decks && currentFragment !is DecksFragment) {
            changeFragment(DecksFragment(), "decks", true)


        } else if (menuItem.itemId == R.id.drawer_rate) {
          //  openRateTheApp()

        } else if (menuItem.itemId == R.id.drawer_beta && currentFragment !is JoinBetaFragment) {
            changeFragment(JoinBetaFragment(), "joinbeta_fragment", true)


        } else if (menuItem.itemId == R.id.drawer_about && currentFragment !is AboutFragment) {
            changeFragment(AboutFragment(), "about_fragment", true)


        } else if (menuItem.itemId == R.id.drawer_release_note) {
            //showReleaseNote();

        } else if (menuItem.itemId == 100) {
            // NB: WARNING, FOR RECREATE DATABASE
            //  recreateDb();

        } else if (menuItem.itemId == 101) {
            CreateDecksAsyncTask(applicationContext).execute()

        } else if (menuItem.itemId == 102) {
            AddFavouritesAsyncTask(applicationContext).execute()

        } else if (menuItem.itemId == 103) {
            throw RuntimeException("This is a crash")

        } else if (menuItem.itemId == 104) {
            //copyDBToSdCard();

        } else if (menuItem.itemId == 105) {
            val copied = FileUtil.copyDbFromSdCard(applicationContext, CardsInfoDbHelper.DATABASE_NAME)
            Toast.makeText(this, if (copied) "database copied" else "database not copied", Toast.LENGTH_LONG).show()
        }

        //navDrawerHelper.closeDrawer();
        return true
    }

    override fun onBackPressed() {
        LOG.d()
        navDrawerHelper?.onBackPressed()
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container) as BasicFragment
        if (current.onBackPressed()) {
            return
        }
        if (current !is MainFragment) {
            changeFragment(MainFragment(), "main", false)
            navDrawerHelper?.resetSelection()
            //   slidingPanelHelper.showPanel();
        } else {
            finish()
        }
    }
}
