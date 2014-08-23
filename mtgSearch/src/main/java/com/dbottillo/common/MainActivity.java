package com.dbottillo.common;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dbottillo.BuildConfig;
import com.dbottillo.adapters.LeftMenuAdapter;
import com.dbottillo.adapters.MTGSetSpinnerAdapter;
import com.dbottillo.base.DBActivity;
import com.dbottillo.base.MTGApp;
import com.dbottillo.database.CardDatabaseHelper;
import com.dbottillo.database.DatabaseHelper;
import com.dbottillo.database.MTGDatabaseHelper;
import com.dbottillo.helper.CreateDBAsyncTask;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.R;
import com.dbottillo.lifecounter.LifeCounterActivity;
import com.dbottillo.resources.GameSet;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.saved.SavedActivity;
import com.dbottillo.view.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends DBActivity implements ActionBar.OnNavigationListener, DBAsyncTask.DBAsyncTaskListener, SlidingUpPanelLayout.PanelSlideListener, AdapterView.OnItemClickListener {

    private static final String PREFERENCE_DATABASE_VERSION = "databaseVersion";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private ArrayList<GameSet> sets;
    private MTGSetSpinnerAdapter setAdapter;

    private SlidingUpPanelLayout slidingPanel;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private LeftMenuAdapter leftMenuAdapter;

    private FilterFragment filterFragment;

    SearchView searchView;
    ImageView arrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDrawerLayout();

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setPanelSlideListener(this);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        if (getSharedPreferences().getInt(PREFERENCE_DATABASE_VERSION, -1) != BuildConfig.DATABASE_VERSION){
            Log.e("MTG", getSharedPreferences().getInt(PREFERENCE_DATABASE_VERSION, -1)+" <-- wrong database version --> "+BuildConfig.DATABASE_VERSION);
            File file = new File(getApplicationInfo().dataDir + "/databases/"+ DatabaseHelper.DATABASE_NAME);
            file.delete();
            CardDatabaseHelper dbHelper = CardDatabaseHelper.getDatabaseHelper(this);
            Toast.makeText(this, getString(R.string.set_loaded, dbHelper.getSets().getCount()), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putInt(PREFERENCE_DATABASE_VERSION, BuildConfig.DATABASE_VERSION);
            editor.apply();
        }

        if (savedInstanceState == null){
            sets = new ArrayList<GameSet>();

            showLoadingInActionBar();
            new DBAsyncTask(this, this, DBAsyncTask.TASK_SET_LIST).execute();
        }else{
            sets = savedInstanceState.getParcelableArrayList("SET");
        }

        setAdapter = new MTGSetSpinnerAdapter(this, sets);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(setAdapter,  this);

        filterFragment = new FilterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.filter, filterFragment)
                .commit();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            handleIntent(intent);
        }
    }

    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                getActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(final View view, final float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
                float value = 0.9f + 0.1f * (1.0f - slideOffset);
                findViewById(R.id.sliding_layout).setScaleX(value);
                findViewById(R.id.sliding_layout).setScaleY(value);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        final ArrayList<LeftMenuAdapter.LeftMenuItem> items = new ArrayList<LeftMenuAdapter.LeftMenuItem>();
        for (LeftMenuAdapter.LeftMenuItem leftMenuItem : LeftMenuAdapter.LeftMenuItem.values()){
            boolean skip = false;
            if (leftMenuItem == LeftMenuAdapter.LeftMenuItem.CREATE_DB && !BuildConfig.DEBUG) skip = true;
            if (leftMenuItem == LeftMenuAdapter.LeftMenuItem.LIFE_COUNTER && !BuildConfig.magic) skip = true;
            if (!skip) {
                items.add(leftMenuItem);
            }
        }

        leftMenuAdapter = new LeftMenuAdapter(this, items);
        mDrawerList.setAdapter(leftMenuAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);
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
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            setIntent(intent);
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent){
        String query = intent.getStringExtra(SearchManager.QUERY);
        getApp().trackEvent(MTGApp.UA_CATEGORY_SEARCH, "done", query);
        if (query.length() < 3){
            Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(query))
                .commit();
        slidingPanel.collapsePane();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
        outState.putParcelableArrayList("SET", sets);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        /*if (!getApp().isPremium() && position > 2){
            showGoToPremium();
            return false;
        }*/
        getApp().trackEvent(MTGApp.UA_CATEGORY_UI, "spinner_selected", sets.get(position).getCode());
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("setPosition", position);
        editor.commit();
        loadSet();
        return true;
    }

    private void loadSet(){
        slidingPanel.collapsePane();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(sets.get(getSharedPreferences().getInt("setPosition", 0))))
                .commit();
    }

    @Override
    public void onTaskFinished(ArrayList<?> result) {
        sets.clear();
        for (Object set : result){
            sets.add((GameSet) set);
        }
        setAdapter.notifyDataSetChanged();
        result.clear();

        hideLoadingFromActionBar();
        getActionBar().setSelectedNavigationItem(getSharedPreferences().getInt("setPosition", 0));
    }

    @Override
    public void onTaskEndWithError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    public SlidingUpPanelLayout getSlidingPanel(){
        return slidingPanel;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        setRotationArrow(180 - (180*slideOffset));
    }

    @Override
    public void onPanelCollapsed(View panel) {
        getApp().trackEvent(MTGApp.UA_CATEGORY_UI, "panel", "collapsed");
        MTGSetFragment setFragment = (MTGSetFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        setFragment.refreshUI();

        setRotationArrow(0);
    }

    @Override
    public void onPanelExpanded(View panel) {
        getApp().trackEvent(MTGApp.UA_CATEGORY_UI, "panel", "expanded");
        setRotationArrow(180);

    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    private void setRotationArrow(float angle){
        if (arrow == null) arrow = (ImageView) findViewById(R.id.arrow_filter);
        else arrow.setRotation(angle);
    }

    public void onToggleClicked(View view) {
        filterFragment.onToggleClicked(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (slidingPanel.isExpanded()){
            slidingPanel.collapsePane();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        slidingPanel.collapsePane();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        loadSet();
                        return true;
                    }
                });

        searchItem.setVisible(!mDrawerLayout.isDrawerOpen(mDrawerList));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showGoToPremium() {
        openDialog("toPremium");
    }

    private void openDialog(String tag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment;
        if (tag.equalsIgnoreCase("about")) {
            newFragment = new AboutFragment();
        }else{
            newFragment = new GoToPremiumFragment();
        }

        newFragment.show(ft, tag);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LeftMenuAdapter.LeftMenuItem item = leftMenuAdapter.getItem(position);
        if (item == LeftMenuAdapter.LeftMenuItem.FAVOURITE){
            startActivity(new Intent(this, SavedActivity.class));

        } else if (item == LeftMenuAdapter.LeftMenuItem.LIFE_COUNTER){
            startActivity(new Intent(this, LifeCounterActivity.class));

        }else if (item == LeftMenuAdapter.LeftMenuItem.ABOUT){
            openDialog("about");

        }else if (item == LeftMenuAdapter.LeftMenuItem.FORCE_UPDATE){
            // /data/data/com.dbottillo.mtgsearch/databases/mtgsearch.db
            getApp().trackEvent(MTGApp.UA_CATEGORY_SEARCH, "reset_db", "");
            sets.clear();
            setAdapter.notifyDataSetChanged();
            File file = new File(getApplicationInfo().dataDir + "/databases/"+ DatabaseHelper.DATABASE_NAME);
            file.delete();
            CardDatabaseHelper dbHelper = CardDatabaseHelper.getDatabaseHelper(this);
            Toast.makeText(this, getString(R.string.set_loaded, dbHelper.getSets().getCount()), Toast.LENGTH_SHORT).show();
            new DBAsyncTask(this, this, DBAsyncTask.TASK_SET_LIST).execute();

        }else if (item == LeftMenuAdapter.LeftMenuItem.CREATE_DB){
            // NB: WARNING, FOR RECREATE DATABASE
            String packageName = getApplication().getPackageName();
            new CreateDBAsyncTask(this,packageName).execute();
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
