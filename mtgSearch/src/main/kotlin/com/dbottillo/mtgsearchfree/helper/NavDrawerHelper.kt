package com.dbottillo.mtgsearchfree.helper

import android.content.res.Configuration
import android.content.res.Resources
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.persistence.GeneralPreferences
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import java.util.*

class NavDrawerHelper {

    internal var drawerLayout: DrawerLayout;
    internal var drawerToggle: ActionBarDrawerToggle;
    var navigationView: NavigationView
    var headerTitle: TextView
    var headerText: TextView
    var resources: Resources

    constructor(activity: MainActivity, toolbar: Toolbar, listener: NavigationView.OnNavigationItemSelectedListener) {
        resources = activity.resources
        drawerLayout = activity.findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            /* Called when a drawer has settled in a completely closed state. */
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                activity.invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            /* Called when a drawer has settled in a completely open state. */
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                activity.invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerSlide(view: View?, slideOffset: Float) {
                super.onDrawerSlide(view, slideOffset)
            }

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    updateHeaderView()
                }
            }
        }

        drawerLayout.setDrawerListener(drawerToggle)

        navigationView = activity.findViewById(R.id.navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(listener)
        navigationView.isSaveEnabled = true

        if (BuildConfig.DEBUG) {
            navigationView.menu?.add(0, 100, Menu.NONE, resources.getString(R.string.action_create_db))
            navigationView.menu?.add(0, 101, Menu.NONE, resources.getString(R.string.action_fill_decks))
            navigationView.menu?.add(0, 102, Menu.NONE, resources.getString(R.string.action_create_fav))
            navigationView.menu?.add(0, 103, Menu.NONE, resources.getString(R.string.action_crash))
        }
        if (GeneralPreferences.with(activity.applicationContext).isDebugEnabled) {
            navigationView.menu?.add(0, 104, Menu.NONE, resources.getString(R.string.action_send_db))
            navigationView.menu?.add(0, 105, Menu.NONE, resources.getString(R.string.action_copy_db))
        }

        val headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header)

        headerTitle = headerLayout?.findViewById(R.id.drawer_header_title) as TextView
        headerText = headerLayout?.findViewById(R.id.drawer_header_text) as TextView
    }

    fun syncState() {
        drawerToggle.syncState()
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        drawerToggle.onConfigurationChanged(newConfig)
    }

    private fun updateHeaderView() {
        val random = Random().nextInt(12)
        headerTitle.text = resources.getStringArray(R.array.header_title_flavor)[random]
        headerText.text = resources.getStringArray(R.array.header_text_flavor)[random]
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle.onOptionsItemSelected(item)
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        drawerLayout.closeDrawers()
    }

    fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    fun resetSelection() {
        for (i in 1..navigationView.menu!!.size() - 1) {
            navigationView.menu.getItem(i).isChecked = false
        }
        navigationView.menu.getItem(0).isChecked = true
        drawerToggle.syncState()
    }

    fun select(i: Int) {
        navigationView.menu?.getItem(i)?.isChecked = true
        drawerToggle.syncState()
    }

    fun getCurrentSelection(): Int {
        for (i in 0..navigationView.menu!!.size() - 1) {
            if (navigationView.menu.getItem(i).isChecked) {
                return i;
            }
        }
        return 0;
    }

}
