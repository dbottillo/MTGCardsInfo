package com.dbottillo.mtgsearchfree.view.helpers;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.activities.MainActivity;

import java.util.Random;

public class NavDrawerHelper {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    TextView headerTitle;
    TextView headerText;
    Resources resources;

    public NavDrawerHelper(final MainActivity activity, NavigationView navigationView, Toolbar toolbar, NavigationView.OnNavigationItemSelectedListener listener) {
        this.navigationView = navigationView;
        this.resources = activity.getResources();
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            /* Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /* Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerSlide(View view, float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
            }

            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_SETTLING && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    updateHeaderView();
                }
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(listener);
        navigationView.setSaveEnabled(true);

        if (BuildConfig.DEBUG) {
            navigationView.getMenu().add(0, 100, Menu.NONE, resources.getString(R.string.action_create_db));
            navigationView.getMenu().add(0, 101, Menu.NONE, resources.getString(R.string.action_fill_decks));
            navigationView.getMenu().add(0, 102, Menu.NONE, resources.getString(R.string.action_create_fav));
            navigationView.getMenu().add(0, 103, Menu.NONE, resources.getString(R.string.action_crash));
        }
        if (GeneralPreferences.with(activity.getApplicationContext()).isDebugEnabled()) {
            navigationView.getMenu().add(0, 104, Menu.NONE, resources.getString(R.string.action_send_db));
            navigationView.getMenu().add(0, 105, Menu.NONE, resources.getString(R.string.action_copy_db));
        }

        View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header);

        headerTitle = (TextView) headerLayout.findViewById(R.id.drawer_header_title);
        headerText = (TextView) headerLayout.findViewById(R.id.drawer_header_text);
        updateHeaderView();
    }

    public void syncState() {
        drawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void updateHeaderView() {
        LOG.d();
        int random = new Random().nextInt(12);
        headerTitle.setText(resources.getStringArray(R.array.header_title_flavor)[random]);
        headerText.setText(resources.getStringArray(R.array.header_text_flavor)[random]);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void resetSelection() {
        LOG.d();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(0).setChecked(true);
        drawerToggle.syncState();
    }

    public void select(int i) {
        LOG.d();
        navigationView.getMenu().getItem(i).setChecked(true);
        drawerToggle.syncState();
    }

    public int getCurrentSelection() {
        LOG.d();
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (navigationView.getMenu().getItem(i).isChecked()) {
                return i;
            }
        }
        return 0;
    }
}
