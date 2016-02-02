package com.dbottillo.mtgsearchfree.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.about.AboutFragment;
import com.dbottillo.mtgsearchfree.about.ReleaseNoteFragment;
import com.dbottillo.mtgsearchfree.cards.CardLuckyActivity;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.decks.DecksFragment;
import com.dbottillo.mtgsearchfree.filter.FilterActivity;
import com.dbottillo.mtgsearchfree.helper.AddFavouritesAsyncTask;
import com.dbottillo.mtgsearchfree.helper.CreateDBAsyncTask;
import com.dbottillo.mtgsearchfree.helper.CreateDecksAsyncTask;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment;
import com.dbottillo.mtgsearchfree.saved.SavedFragment;
import com.dbottillo.mtgsearchfree.search.SearchActivity;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import java.io.File;
import java.util.Random;

public class MainActivity extends FilterActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_SELECTION = "currentSelection";

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
            if (savedInstanceState.getInt(CURRENT_SELECTION) > 0) {
                getSlidingPanel().setPanelHeight(0);
            }
        }

        checkPushReleaseNoteClicked(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkPushReleaseNoteClicked(intent);
    }

    private void checkPushReleaseNoteClicked(Intent intent) {
        if (intent.hasExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH)) {
            if (intent.getBooleanExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH, false)) {
                TrackingHelper.getInstance(getApplicationContext()).trackEvent(TrackingHelper.UA_CATEGORY_RELEASE_NOTE, TrackingHelper.UA_ACTION_OPEN, "push");
                showReleaseNoteFragment();
                navigationView.getMenu().getItem(6).setChecked(true);
                mDrawerToggle.syncState();
            }
            intent.removeExtra(MTGApp.INTENT_RELEASE_NOTE_PUSH);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTION, findSelectedPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(CURRENT_SELECTION)) {
            Menu menu = navigationView.getMenu();
            menu.getItem(savedInstanceState.getInt(CURRENT_SELECTION)).setChecked(true);
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
                if (newState == DrawerLayout.STATE_SETTLING && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    updateHeaderView();
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
            navigationView.getMenu().add(0, 101, Menu.NONE, getString(R.string.action_fill_decks));
            navigationView.getMenu().add(0, 102, Menu.NONE, getString(R.string.action_create_fav));
            navigationView.getMenu().add(0, 103, Menu.NONE, getString(R.string.action_crash));
        }
        if (BuildConfig.COPY_DB) {
            navigationView.getMenu().add(0, 104, Menu.NONE, getString(R.string.action_send_db));
            navigationView.getMenu().add(0, 105, Menu.NONE, getString(R.string.action_send_db));
        }

        View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header);

        headerTitle = (TextView) headerLayout.findViewById(R.id.drawer_header_title);
        headerText = (TextView) headerLayout.findViewById(R.id.drawer_header_text);
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
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_lucky) {
            startActivity(new Intent(this, CardLuckyActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        DBFragment currentFragment = (DBFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (menuItem.getItemId() == R.id.drawer_home && !(currentFragment instanceof MainFragment)) {
            changeFragment(new MainFragment(), "main", false);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), getResources().getDimensionPixelSize(R.dimen.collapsedHeight));

        } else if (menuItem.getItemId() == R.id.drawer_saved && !(currentFragment instanceof SavedFragment)) {
            changeFragment(SavedFragment.newInstance(), "saved_fragment", true);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), getResources().getDimensionPixelSize(R.dimen.collapsedHeight));

        } else if (menuItem.getItemId() == R.id.drawer_life_counter && !(currentFragment instanceof LifeCounterFragment)) {
            changeFragment(LifeCounterFragment.newInstance(), "life_counter", true);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), 0);

        } else if (menuItem.getItemId() == R.id.drawer_decks && !(currentFragment instanceof DecksFragment)) {
            changeFragment(DecksFragment.newInstance(), "decks", true);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), 0);

        } else if (menuItem.getItemId() == R.id.drawer_rate) {
            openRateTheApp();

        } else if (menuItem.getItemId() == R.id.drawer_about && !(currentFragment instanceof AboutFragment)) {
            changeFragment(new AboutFragment(), "about_fragment", true);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), 0);

        } else if (menuItem.getItemId() == R.id.drawer_release_note) {
            showReleaseNoteFragment();

        } else if (menuItem.getItemId() == 100) {
            // NB: WARNING, FOR RECREATE DATABASE
            String packageName = getApplication().getPackageName();
            new CreateDBAsyncTask(this, packageName).execute();

        } else if (menuItem.getItemId() == 101) {
            new CreateDecksAsyncTask(this.getApplicationContext()).execute();

        } else if (menuItem.getItemId() == 102) {
            new AddFavouritesAsyncTask(this.getApplicationContext()).execute();

        } else if (menuItem.getItemId() == 103) {
            throw new RuntimeException("This is a crash");

        } else if (menuItem.getItemId() == 104) {
            File file = FileUtil.copyDbToSdCard(getApp().getApplicationContext(), CardsInfoDbHelper.DATABASE_NAME);
            if (file != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"help@mtgcardsinfo.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[MTGCardsInfo] Database status");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(intent, "Send mail...."));
            }
        } else if (menuItem.getItemId() == 105) {
            boolean copied = FileUtil.copyDbFromSdCard(getApp().getApplicationContext(), CardsInfoDbHelper.DATABASE_NAME);
            Toast.makeText(this, copied ? "database copied" : "database not copied", Toast.LENGTH_LONG).show();
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    private void showReleaseNoteFragment() {
        changeFragment(new ReleaseNoteFragment(), "release_note_fragment", true);
        AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), 0);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        DBFragment currentFragment = (DBFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment.onBackPressed()) {
            return;
        }
        boolean isMainFragment = currentFragment instanceof MainFragment;
        if (!isMainFragment) {
            changeFragment(new MainFragment(), "main", false);
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
            navigationView.getMenu().getItem(0).setChecked(true);
            AnimationUtil.animateSlidingPanelHeight(getSlidingPanel(), getResources().getDimensionPixelSize(R.dimen.collapsedHeight));
        } else {
            finish();
        }
    }
}
