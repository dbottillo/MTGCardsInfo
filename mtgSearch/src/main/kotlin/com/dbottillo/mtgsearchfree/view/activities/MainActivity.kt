package com.dbottillo.mtgsearchfree.view.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.about.AboutFragment
import com.dbottillo.mtgsearchfree.about.JoinBetaFragment
import com.dbottillo.mtgsearchfree.about.ReleaseNoteFragment
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.cards.CardLuckyActivity
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.decks.DecksFragment
import com.dbottillo.mtgsearchfree.helper.*
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.presenter.MainActivityPresenter
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.saved.SavedFragment
import com.dbottillo.mtgsearchfree.search.SearchActivity
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.MainView
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment
import com.dbottillo.mtgsearchfree.view.views.FilterPickerView
import javax.inject.Inject

class MainActivity : BasicActivity(), MainView, CardFilterView,
        NavigationView.OnNavigationItemSelectedListener, FilterPickerView.OnFilterPickerListener,
        SlidingPanelHelper.SlidingPanelHelperListener {

    interface MainActivityListener {
        fun updateContent()
    }

    val CURRENT_SELECTION = "currentSelection"

    var mainPresenter: MainActivityPresenter? = null
    var slidingPanelHelper: SlidingPanelHelper? = null
    var navDrawerHelper: NavDrawerHelper? = null
    var listener: MainActivityListener? = null
    val filterView: FilterPickerView by bindView(R.id.filter)
    var filterLoaded: Boolean = false
    var initialBundle: Bundle? = null
    var currentFilter: CardFilter? = null

    @Inject lateinit var filterPresenter: CardFilterPresenter

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this)

        setupToolbar();
        slidingPanelHelper = SlidingPanelHelper(findViewById(R.id.sliding_layout) as SlidingUpPanelLayout, resources, this)
        slidingPanelHelper?.init(filterView.findViewById(R.id.filter_draggable))
        navDrawerHelper = NavDrawerHelper(this, toolbar!!, this)

        initialBundle = bundle

        MTGApp.Companion.filterGraph.inject(this)
        filterPresenter.init(this)
        if (bundle == null) {
            filterPresenter.loadFilter()
        } else {
            currentFilter = bundle.getParcelable("currentFilter")
            filterView.refresh(currentFilter!!);
            filterLoaded = true
        }

        mainPresenter = MainActivityPresenter(this)
        mainPresenter?.checkReleaseNote(intent);

        filterView.setFilterPickerListener(this)

        if (bundle != null && bundle.getInt(CURRENT_SELECTION) > 0) {
            slidingPanelHelper?.hidePanel(true)
        }
    }

    override fun filterLoaded(filter: CardFilter) {
        currentFilter = filter
        LOG.e("filter loaded")
        if (!filterLoaded) {
            if (initialBundle == null) {
                changeFragment(MainFragment(), "main", false);
            }
        } else {
            LOG.e("needs to update the content")
            listener?.updateContent()
        }
        filterView.refresh(filter);
    }

    override fun getPageTrack(): String {
        return "/main";
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        mainPresenter?.checkReleaseNote(intent);
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_SELECTION, navDrawerHelper!!.getCurrentSelection())
        outState?.putParcelable("currentFilter", currentFilter)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navDrawerHelper?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navDrawerHelper?.onConfigurationChanged(newConfig)
    }

    override fun showReleaseNote() {
        TrackingHelper.getInstance(applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "push")
        changeFragment(ReleaseNoteFragment(), "release_note_fragment", true)
        slidingPanelHelper?.hidePanel(true)
        navDrawerHelper?.select(6)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navDrawerHelper?.onOptionsItemSelected(item) as Boolean) {
            return true
        }
        if (item.itemId == android.R.id.home) {
            navDrawerHelper?.openDrawer();
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
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as BasicFragment
        if (menuItem.itemId == R.id.drawer_home && currentFragment !is MainFragment) {
            changeFragment(MainFragment(), "main", false)
            slidingPanelHelper?.showPanel()

        } else if (menuItem.itemId == R.id.drawer_saved && currentFragment !is SavedFragment) {
            changeFragment(SavedFragment.newInstance(), "saved_fragment", true)
            slidingPanelHelper?.showPanel()

        } else if (menuItem.itemId == R.id.drawer_life_counter && currentFragment !is LifeCounterFragment) {
            changeFragment(LifeCounterFragment.newInstance(), "life_counter", true)
            slidingPanelHelper?.hidePanel(true)

        } else if (menuItem.itemId == R.id.drawer_decks && currentFragment !is DecksFragment) {
            changeFragment(DecksFragment.newInstance(), "decks", true)
            slidingPanelHelper?.hidePanel(true)

        } else if (menuItem.itemId == R.id.drawer_rate) {
            openRateTheApp()

        } else if (menuItem.itemId == R.id.drawer_beta) {
            changeFragment(JoinBetaFragment(), "joinbeta_fragment", true)
            slidingPanelHelper?.hidePanel(true)

        } else if (menuItem.itemId == R.id.drawer_about && currentFragment !is AboutFragment) {
            changeFragment(AboutFragment(), "about_fragment", true)
            slidingPanelHelper?.hidePanel(true)

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

        navDrawerHelper?.closeDrawer()
        return true
    }

    override fun onBackPressed() {
        if (slidingPanelHelper!!.onBackPressed()) {
            return;
        }
        navDrawerHelper?.onBackPressed()
        var current = supportFragmentManager.findFragmentById(R.id.fragment_container) as BasicFragment
        if (current.onBackPressed()) {
            return;
        }
        if (current !is MainFragment) {
            changeFragment(MainFragment(), "main", false)
            navDrawerHelper?.resetSelection()
            slidingPanelHelper?.showPanel()
        } else {
            finish();
        }
    }

    fun setMainActivityListener(list: MainActivityListener) {
        listener = list;
    }

    override fun filterUpdated(type: CardFilter.TYPE, on: Boolean) {
        filterPresenter.update(type, on)
    }

    override fun onPanelChangeOffset(offset: Float) {
        filterView.onPanelSlide(offset)
    }
}