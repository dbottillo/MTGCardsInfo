package com.dbottillo.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.cards.CardLuckyActivity;
import com.dbottillo.dialog.AboutFragment;
import com.dbottillo.filter.FilterActivity;
import com.dbottillo.helper.CreateDBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.lifecounter.LifeCounterFragment;
import com.dbottillo.saved.SavedFragment;
import com.dbottillo.search.SearchActivity;
import com.dbottillo.util.AnimationUtil;

import java.util.Random;

public class MainActivity extends FilterActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SEARCH_REQUEST_CODE = 100;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupDrawerLayout();
        setupSlidingPanel();

        if (savedInstanceState == null) {
            changeFragment(new MainFragment(), "main", false);
        } else {
            if (savedInstanceState.getInt("currentSelection") > 0) {
                getSlidingPanel().setPanelHeight(0);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentSelection", findSelectedPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("currentSelection")) {
            Menu menu = navigationView.getMenu();
            menu.getItem(savedInstanceState.getInt("currentSelection")).setChecked(true);
        }
    }

    private int findSelectedPosition() {
        Menu menu = navigationView.getMenu();
        int count = menu.size();
        for (int i = 0; i < count; i++) {
            if (menu.getItem(i).isChecked()) {
                return i;
            }
        }
        return -1;
    }

    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            /* Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /* Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(final View view, final float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        updateHeaderView();
                    }
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setSaveEnabled(true);

        if (BuildConfig.DEBUG) {
            navigationView.getMenu().add(0, 100, Menu.NONE, getString(R.string.action_create_db));
            navigationView.getMenu().add(0, 101, Menu.NONE, getString(R.string.action_crash));
        }

        headerTitle = (TextView) findViewById(R.id.drawer_header_title);
        headerText = (TextView) findViewById(R.id.drawer_header_text);
    }

    TextView headerTitle;
    TextView headerText;

    private void updateHeaderView() {
        int random = new Random().nextInt(12);
        headerTitle.setText(getResources().getStringArray(R.array.header_title_flavor)[random]);
        headerText.setText(getResources().getStringArray(R.array.header_text_flavor)[random]);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public String getPageTrack() {
        return "/main";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.action_search) {
            startActivityForResult(new Intent(this, SearchActivity.class), SEARCH_REQUEST_CODE);
            return true;
        }
        if (item.getItemId() == R.id.action_lucky) {
            startActivity(new Intent(this, CardLuckyActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_REQUEST_CODE) {
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MainFragment) {
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                mainFragment.updateSetFragment();
            }
            getFilterFragment().updateFilterUI();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (!menuItem.isChecked()) {
            menuItem.setChecked(true);

            if (menuItem.getItemId() == R.id.drawer_home) {
                changeFragment(new MainFragment(), "main", false);
                AnimationUtil.animteSlidingPanelHeight(getSlidingPanel(), getResources().getDimensionPixelSize(R.dimen.collapsedHeight));

            } else if (menuItem.getItemId() == R.id.drawer_saved) {
                changeFragment(SavedFragment.newInstance(), "saved_fragment", true);
                AnimationUtil.animteSlidingPanelHeight(getSlidingPanel(), 0);

            } else if (menuItem.getItemId() == R.id.drawer_life_counter) {
                changeFragment(LifeCounterFragment.newInstance(), "life_counter", true);
                AnimationUtil.animteSlidingPanelHeight(getSlidingPanel(), 0);

            } else if (menuItem.getItemId() == R.id.drawer_rate) {
                openRateTheApp();

            } else if (menuItem.getItemId() == R.id.drawer_about) {
                changeFragment(new AboutFragment(), "about_fragment", true);
                AnimationUtil.animteSlidingPanelHeight(getSlidingPanel(), 0);

            } else if (menuItem.getItemId() == 100) {

                // NB: WARNING, FOR RECREATE DATABASE
                String packageName = getApplication().getPackageName();
                new CreateDBAsyncTask(this, packageName).execute();

            } else if (menuItem.getItemId() == 101) {
                throw new RuntimeException("This is a crash");
            }
        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        boolean isMainFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MainFragment;
        if (!isMainFragment) {
            changeFragment(new MainFragment(), "main", false);
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
            navigationView.getMenu().getItem(0).setChecked(true);
            AnimationUtil.animteSlidingPanelHeight(getSlidingPanel(), getResources().getDimensionPixelSize(R.dimen.collapsedHeight));
        } else {
            finish();
        }
    }
}
