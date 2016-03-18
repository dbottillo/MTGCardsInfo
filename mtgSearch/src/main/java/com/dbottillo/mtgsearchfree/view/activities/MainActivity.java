package com.dbottillo.mtgsearchfree.view.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.decks.DecksFragment;
import com.dbottillo.mtgsearchfree.helper.AddFavouritesAsyncTask;
import com.dbottillo.mtgsearchfree.helper.CreateDBAsyncTask;
import com.dbottillo.mtgsearchfree.helper.CreateDecksAsyncTask;
import com.dbottillo.mtgsearchfree.helper.NavDrawerHelper;
import com.dbottillo.mtgsearchfree.helper.SlidingPanelHelper;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter;
import com.dbottillo.mtgsearchfree.presenter.MainActivityPresenter;
import com.dbottillo.mtgsearchfree.saved.SavedFragment;
import com.dbottillo.mtgsearchfree.search.SearchActivity;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.view.CardFilterView;
import com.dbottillo.mtgsearchfree.view.MainView;
import com.dbottillo.mtgsearchfree.view.SlidingUpPanelLayout;
import com.dbottillo.mtgsearchfree.view.fragments.AboutFragment;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.view.fragments.JoinBetaFragment;
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment;
import com.dbottillo.mtgsearchfree.view.fragments.ReleaseNoteFragment;
import com.dbottillo.mtgsearchfree.view.views.FilterPickerView;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BasicActivity implements MainView, CardFilterView,
        NavigationView.OnNavigationItemSelectedListener, FilterPickerView.OnFilterPickerListener,
        SlidingPanelHelper.SlidingPanelHelperListener {

    public interface MainActivityListener {
        void updateContent();
    }

    private static final String CURRENT_SELECTION = "currentSelection";

    MainActivityPresenter mainPresenter;
    SlidingPanelHelper slidingPanelHelper;
    NavDrawerHelper navDrawerHelper;
    MainActivityListener listener;

    @Bind(R.id.filter)
    FilterPickerView filterView;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    boolean filterLoaded;
    Bundle initialBundle;

    public CardFilter getCurrentFilter() {
        return currentFilter;
    }

    CardFilter currentFilter;

    @Inject
    CardFilterPresenter filterPresenter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupToolbar();
        slidingPanelHelper = new SlidingPanelHelper(slidingUpPanelLayout, getResources(), this);
        slidingPanelHelper.init(filterView.findViewById(R.id.filter_draggable));
        navDrawerHelper = new NavDrawerHelper(this, toolbar, this);

        initialBundle = bundle;

        MTGApp.dataGraph.inject(this);
        filterPresenter.init(this);

        if (bundle == null) {
            filterPresenter.loadFilter();
        } else {
            currentFilter = bundle.getParcelable("currentFilter");
            filterView.refresh(currentFilter);
            filterLoaded = true;
        }

        mainPresenter = new MainActivityPresenter(this);
        mainPresenter.checkReleaseNote(getIntent());

        filterView.setFilterPickerListener(this);

        if (bundle != null && bundle.getInt(CURRENT_SELECTION) > 0) {
            slidingPanelHelper.hidePanel(true);
        }
    }

    public void filterLoaded(CardFilter filter) {
        currentFilter = filter;
        if (!filterLoaded) {
            if (initialBundle == null) {
                changeFragment(new MainFragment(), "main", false);
            }
        } else {
            listener.updateContent();
        }
        filterView.refresh(filter);
    }

    public String getPageTrack() {
        return "/main";
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mainPresenter.checkReleaseNote(intent);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTION, navDrawerHelper.getCurrentSelection());
        outState.putParcelable("currentFilter", currentFilter);
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navDrawerHelper.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navDrawerHelper.onConfigurationChanged(newConfig);
    }

    public void showReleaseNote() {
        TrackingHelper.getInstance(getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "push");
        changeFragment(new ReleaseNoteFragment(), "release_note_fragment", true);
        slidingPanelHelper.hidePanel(true);
        ;
        navDrawerHelper.select(6);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (navDrawerHelper.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            navDrawerHelper.openDrawer();
            return true;
        }
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_lucky) {
            startActivity(new Intent(this, CardLuckyActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        BasicFragment currentFragment = (BasicFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (menuItem.getItemId() == R.id.drawer_home && currentFragment instanceof MainFragment) {
            changeFragment(new MainFragment(), "main", false);
            slidingPanelHelper.showPanel();
        } else if (menuItem.getItemId() == R.id.drawer_saved && currentFragment instanceof SavedFragment) {
            changeFragment(SavedFragment.newInstance(), "saved_fragment", true);
            slidingPanelHelper.showPanel();

        } else if (menuItem.getItemId() == R.id.drawer_life_counter && currentFragment instanceof LifeCounterFragment) {
            changeFragment(LifeCounterFragment.newInstance(), "life_counter", true);
            slidingPanelHelper.hidePanel(true);

        } else if (menuItem.getItemId() == R.id.drawer_decks && currentFragment instanceof DecksFragment) {
            changeFragment(DecksFragment.newInstance(), "decks", true);
            slidingPanelHelper.hidePanel(true);

        } else if (menuItem.getItemId() == R.id.drawer_rate) {
            openRateTheApp();

        } else if (menuItem.getItemId() == R.id.drawer_beta) {
            changeFragment(new JoinBetaFragment(), "joinbeta_fragment", true);
            slidingPanelHelper.hidePanel(true);

        } else if (menuItem.getItemId() == R.id.drawer_about && currentFragment instanceof AboutFragment) {
            changeFragment(new AboutFragment(), "about_fragment", true);
            slidingPanelHelper.hidePanel(true);

        } else if (menuItem.getItemId() == R.id.drawer_release_note) {
            showReleaseNote();

        } else if (menuItem.getItemId() == 100) {
            // NB: WARNING, FOR RECREATE DATABASE
            new CreateDBAsyncTask(this, getApplication().getPackageName()).execute();

        } else if (menuItem.getItemId() == 101) {
            new CreateDecksAsyncTask(getApplicationContext()).execute();

        } else if (menuItem.getItemId() == 102) {
            new AddFavouritesAsyncTask(getApplicationContext()).execute();

        } else if (menuItem.getItemId() == 103) {
            throw new RuntimeException("This is a crash");

        } else if (menuItem.getItemId() == 104) {
            File file = FileUtil.copyDbToSdCard(getApplicationContext(), CardsInfoDbHelper.DATABASE_NAME);
            if (file != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@mtgcardsinfo.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[MTGCardsInfo] Database status");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(intent, "Send mail...."));
            }
        } else if (menuItem.getItemId() == 105) {
            boolean copied = FileUtil.copyDbFromSdCard(getApplicationContext(), CardsInfoDbHelper.DATABASE_NAME);
            Toast.makeText(this, (copied) ? "database copied" : "database not copied", Toast.LENGTH_LONG).show();
        }

        navDrawerHelper.closeDrawer();
        return true;
    }

    public void onBackPressed() {
        if (slidingPanelHelper.onBackPressed()) {
            return;
        }
        navDrawerHelper.onBackPressed();
        BasicFragment current = (BasicFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current.onBackPressed()) {
            return;
        }
        if (current instanceof MainFragment) {
            changeFragment(new MainFragment(), "main", false);
            navDrawerHelper.resetSelection();
            slidingPanelHelper.showPanel();
        } else {
            finish();
        }
    }

    public void setMainActivityListener(MainActivityListener list) {
        listener = list;
    }

    public void filterUpdated(CardFilter.TYPE type, boolean on) {
        filterPresenter.update(type, on);
    }

    public void onPanelChangeOffset(float offset) {
        filterView.onPanelSlide(offset);
    }

}