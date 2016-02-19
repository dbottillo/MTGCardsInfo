package com.dbottillo.mtgsearchfree.view.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.about.AboutFragment
import com.dbottillo.mtgsearchfree.about.JoinBetaFragment
import com.dbottillo.mtgsearchfree.about.ReleaseNoteFragment
import com.dbottillo.mtgsearchfree.cards.CardLuckyActivity
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.decks.DecksFragment
import com.dbottillo.mtgsearchfree.helper.AddFavouritesAsyncTask
import com.dbottillo.mtgsearchfree.helper.CreateDBAsyncTask
import com.dbottillo.mtgsearchfree.helper.CreateDecksAsyncTask
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.persistence.GeneralPreferences
import com.dbottillo.mtgsearchfree.presenter.MainActivityPresenter
import com.dbottillo.mtgsearchfree.saved.SavedFragment
import com.dbottillo.mtgsearchfree.search.SearchActivity
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.view.MainView
import com.dbottillo.mtgsearchfree.view.fragments.DBFragment
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment
import java.util.*

class MainActivity : FilterActivity(), MainView, NavigationView.OnNavigationItemSelectedListener {

    val CURRENT_SELECTION = "currentSelection"

    var mainPresenter: MainActivityPresenter? = null
    var drawerLayout: DrawerLayout? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    var navigationView: NavigationView? = null
    var headerTitle: TextView? = null
    var headerText: TextView? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupSlidingPanel();
        setupDrawerLayout();

        if (bundle == null) {
            changeFragment(MainFragment(), "main", false);
        } else {
            if (bundle.getInt(CURRENT_SELECTION) > 0) {
                slidingPanel?.setPanelHeight(0);
            }
        }

        mainPresenter = MainActivityPresenter(this)
        mainPresenter?.checkReleaseNote(intent);
    }

    override fun getPageTrack(): String {
        return "/main";
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        mainPresenter?.checkReleaseNote(intent);
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun showReleaseNote() {
        TrackingHelper.getInstance(applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "push")
        changeFragment(ReleaseNoteFragment(), "release_note_fragment", true)
        AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)
        navigationView?.menu?.getItem(6)?.isChecked = true
        drawerToggle?.syncState()
    }

    private fun setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            /* Called when a drawer has settled in a completely closed state. */
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            /* Called when a drawer has settled in a completely open state. */
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerSlide(view: View?, slideOffset: Float) {
                super.onDrawerSlide(view, slideOffset)
            }

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING && !drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                    updateHeaderView()
                }
            }
        }

        // Set the drawer toggle as the DrawerListener
        drawerLayout?.setDrawerListener(drawerToggle)

        navigationView = findViewById(R.id.navigation_view) as NavigationView
        navigationView?.setNavigationItemSelectedListener(this)
        navigationView?.isSaveEnabled = true

        if (BuildConfig.DEBUG) {
            navigationView?.menu?.add(0, 100, Menu.NONE, getString(R.string.action_create_db))
            navigationView?.menu?.add(0, 101, Menu.NONE, getString(R.string.action_fill_decks))
            navigationView?.menu?.add(0, 102, Menu.NONE, getString(R.string.action_create_fav))
            navigationView?.menu?.add(0, 103, Menu.NONE, getString(R.string.action_crash))
        }
        if (GeneralPreferences.with(applicationContext).isDebugEnabled) {
            navigationView?.menu?.add(0, 104, Menu.NONE, getString(R.string.action_send_db))
            navigationView?.menu?.add(0, 105, Menu.NONE, getString(R.string.action_copy_db))
        }

        val headerLayout = navigationView?.inflateHeaderView(R.layout.drawer_header)

        headerTitle = headerLayout?.findViewById(R.id.drawer_header_title) as TextView
        headerText = headerLayout?.findViewById(R.id.drawer_header_text) as TextView
    }

    private fun updateHeaderView() {
        val random = Random().nextInt(12)
        headerTitle?.text = resources.getStringArray(R.array.header_title_flavor)[random]
        headerText?.text = resources.getStringArray(R.array.header_text_flavor)[random]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle?.onOptionsItemSelected(item) as Boolean) {
            return true
        }
        if (item.itemId == android.R.id.home) {
            drawerLayout?.openDrawer(GravityCompat.START)
            return true
        }
        if (item.itemId == R.id.action_search) {
            startActivity(Intent(this, SearchActivity::class.java))
            return true
        }
        if (item.itemId == R.id.action_lucky) {
            startActivity(Intent(this, CardLuckyActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as DBFragment
        if (menuItem.itemId == R.id.drawer_home && currentFragment !is MainFragment) {
            changeFragment(MainFragment(), "main", false)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, resources.getDimensionPixelSize(R.dimen.collapsedHeight))

        } else if (menuItem.itemId == R.id.drawer_saved && currentFragment !is SavedFragment) {
            changeFragment(SavedFragment.newInstance(), "saved_fragment", true)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, resources.getDimensionPixelSize(R.dimen.collapsedHeight))

        } else if (menuItem.itemId == R.id.drawer_life_counter && currentFragment !is LifeCounterFragment) {
            changeFragment(LifeCounterFragment.newInstance(), "life_counter", true)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)

        } else if (menuItem.itemId == R.id.drawer_decks && currentFragment !is DecksFragment) {
            changeFragment(DecksFragment.newInstance(), "decks", true)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)

        } else if (menuItem.itemId == R.id.drawer_rate) {
            openRateTheApp()

        } else if (menuItem.itemId == R.id.drawer_beta) {
            changeFragment(JoinBetaFragment(), "joinbeta_fragment", true)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)

        } else if (menuItem.itemId == R.id.drawer_about && currentFragment !is AboutFragment) {
            changeFragment(AboutFragment(), "about_fragment", true)
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, 0)

        } else if (menuItem.itemId == R.id.drawer_release_note) {
            showReleaseNote()

        } else if (menuItem.itemId == 100) {
            // NB: WARNING, FOR RECREATE DATABASE
            val packageName = application.packageName
            CreateDBAsyncTask(this, packageName).execute()

        } else if (menuItem.itemId == 101) {
            CreateDecksAsyncTask(this.applicationContext).execute()

        } else if (menuItem.itemId == 102) {
            AddFavouritesAsyncTask(this.applicationContext).execute()

        } else if (menuItem.itemId == 103) {
            throw RuntimeException("This is a crash")

        } else if (menuItem.itemId == 104) {
            val file = FileUtil.copyDbToSdCard(app?.applicationContext, CardsInfoDbHelper.DATABASE_NAME)
            if (file != null) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help@mtgcardsinfo.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "[MTGCardsInfo] Database status")
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                startActivity(Intent.createChooser(intent, "Send mail...."))
            }
        } else if (menuItem.itemId == 105) {
            val copied = FileUtil.copyDbFromSdCard(app?.applicationContext, CardsInfoDbHelper.DATABASE_NAME)
            Toast.makeText(this, if (copied) "database copied" else "database not copied", Toast.LENGTH_LONG).show()
        }

        drawerLayout?.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout?.closeDrawer(GravityCompat.START);
        }
        var current = supportFragmentManager.findFragmentById(R.id.fragment_container) as DBFragment
        if (current.onBackPressed()) {
            return;
        }
        if (current !is MainFragment) {
            changeFragment(MainFragment(), "main", false)
            for (i in 1..navigationView!!.menu!!.size()-1) {
                navigationView!!.menu.getItem(i).isChecked = false
            }
            navigationView!!.menu.getItem(0).isChecked = false
            AnimationUtil.animateSlidingPanelHeight(slidingPanel, resources.getDimensionPixelSize(R.dimen.collapsedHeight));
        } else {
            finish();
        }
    }
}