package com.dbottillo.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.adapters.GameSetAdapter;
import com.dbottillo.adapters.LeftMenuAdapter;
import com.dbottillo.cards.CardLuckyActivity;
import com.dbottillo.cards.MTGSetFragment;
import com.dbottillo.filter.FilterActivity;
import com.dbottillo.helper.CreateDBAsyncTask;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.lifecounter.LifeCounterActivity;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.saved.SavedActivity;
import com.dbottillo.search.SearchActivity;
import com.dbottillo.view.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends FilterActivity implements DBAsyncTask.DBAsyncTaskListener, SlidingUpPanelLayout.PanelSlideListener, AdapterView.OnItemClickListener {

    private static final int SEARCH_REQUEST_CODE = 100;

    private ArrayList<MTGSet> sets;
    private GameSetAdapter setAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private LeftMenuAdapter leftMenuAdapter;
    private ImageView setArrow;
    private View setListBg;
    private ListView setList;
    private View container;

    private int currentSetPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDrawerLayout();
        setupSlidingPanel(this);

        container = findViewById(R.id.container);
        setListBg = findViewById(R.id.set_list_bg);
        setList = (ListView) findViewById(R.id.set_list);
        setArrow = (ImageView) findViewById(R.id.set_arrow);

        setListBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setList.getHeight() > 0) {
                    showHideSetList(false);
                }
            }
        });

        // Set up the action bar to show a dropdown list.
        getSupportActionBar().setTitle(R.string.app_long_name);

        if (savedInstanceState == null) {
            sets = new ArrayList<MTGSet>();
            new DBAsyncTask(this, this, DBAsyncTask.TASK_SET_LIST).execute();

        } else {
            sets = savedInstanceState.getParcelableArrayList("SET");
            currentSetPosition = savedInstanceState.getInt("currentSetPosition");
            if (currentSetPosition < 0) {
                new DBAsyncTask(this, this, DBAsyncTask.TASK_SET_LIST).execute();
            } else {
                loadSet();
            }
        }

        setAdapter = new GameSetAdapter(this, sets);
        setAdapter.setCurrent(currentSetPosition);
        setList.setAdapter(setAdapter);
        setList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentSetPosition != position) {
                    currentSetPosition = position;
                    showHideSetList(true);
                } else {
                    showHideSetList(false);
                }
            }
        });

        findViewById(R.id.set_chooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideSetList(false);
            }
        });
    }

    private void showHideSetList(final boolean loadSet) {
        final int startHeight = setList.getHeight();
        final int targetHeight = (startHeight == 0) ? container.getHeight() : 0;
        final float startRotation = setArrow.getRotation();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                if (targetHeight > startHeight) {
                    int newHeight = (int) (startHeight + (interpolatedTime * targetHeight));
                    setHeightView(setList, newHeight);
                    setHeightView(setListBg, newHeight);
                    setArrow.setRotation(startRotation + (180 * interpolatedTime));
                } else {
                    int newHeight = (int) (startHeight - startHeight * interpolatedTime);
                    setHeightView(setList, newHeight);
                    setHeightView(setListBg, newHeight);
                    setArrow.setRotation(startRotation - (180 * interpolatedTime));
                }
            }
        };
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (loadSet) {
                    /*if (!getApp().isPremium() && position > 2){
                        showGoToPremium();
                        return false;
                    }*/
                    TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_SELECT, sets.get(currentSetPosition).getCode());
                    SharedPreferences.Editor editor = getSharedPreferences().edit();
                    editor.putInt("setPosition", currentSetPosition);
                    editor.apply();
                    setAdapter.setCurrent(currentSetPosition);
                    setAdapter.notifyDataSetChanged();
                    loadSet();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);

    }

    private void setHeightView(View view, int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = value;
        view.setLayoutParams(params);
    }

    private void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
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
                /*float value = 0.9f + 0.1f * (1.0f - slideOffset);
                findViewById(R.id.sliding_layout).setScaleX(value);
                findViewById(R.id.sliding_layout).setScaleY(value);*/
            }
        };

        /*mDrawerToggle.


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {

            *//** Called when a drawer has settled in a completely closed state. *//*
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            *//** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(final View view, final float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
                float value = 0.9f + 0.1f * (1.0f - slideOffset);
                findViewById(R.id.sliding_layout).setScaleX(value);
                findViewById(R.id.sliding_layout).setScaleY(value);
            }
        };*/

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final ArrayList<LeftMenuAdapter.LeftMenuItem> items = new ArrayList<LeftMenuAdapter.LeftMenuItem>();
        for (LeftMenuAdapter.LeftMenuItem leftMenuItem : LeftMenuAdapter.LeftMenuItem.values()) {
            boolean skip = false;
            if (leftMenuItem == LeftMenuAdapter.LeftMenuItem.CREATE_DB && !BuildConfig.DEBUG)
                skip = true;
            if (leftMenuItem == LeftMenuAdapter.LeftMenuItem.FORCE_CRASH && !BuildConfig.DEBUG)
                skip = true;
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentSetPosition", currentSetPosition);
        outState.putParcelableArrayList("SET", sets);
    }

    private void loadSet() {
        TextView chooserName = ((TextView) findViewById(R.id.set_chooser_name));
        if (!isFinishing() && chooserName != null) {
            collapseSlidingPanel();
            chooserName.setText(sets.get(currentSetPosition).getName());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MTGSetFragment.newInstance(sets.get(currentSetPosition)))
                    .commit();
        }
    }

    @Override
    public void onTaskFinished(int type, ArrayList<?> result) {
        currentSetPosition = getSharedPreferences().getInt("setPosition", 0);
        setAdapter.setCurrent(currentSetPosition);

        sets.clear();
        for (Object set : result) {
            sets.add((MTGSet) set);
        }
        setAdapter.notifyDataSetChanged();
        result.clear();

        loadSet();
    }

    @Override
    public void onTaskEndWithError(int type, String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "set-main", error);
    }


    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        setRotationArrow(180 - (180 * slideOffset));
    }

    @Override
    public void onPanelCollapsed(View panel) {
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "collapsed");
        updateSetFragment();
        setRotationArrow(0);
    }

    @Override
    public void onPanelExpanded(View panel) {
        TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "expanded");
        setRotationArrow(180);
    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
            inflater.inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
            getFilterFragment().updateFilterUI();
            updateSetFragment();
        }
    }

    private void updateSetFragment() {
        MTGSetFragment setFragment = (MTGSetFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        setFragment.refreshUI();
    }

    private void showGoToPremium() {
        openDialog(DBDialog.PREMIUM);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LeftMenuAdapter.LeftMenuItem item = leftMenuAdapter.getItem(position);
        if (item == LeftMenuAdapter.LeftMenuItem.FAVOURITE) {
            startActivity(new Intent(this, SavedActivity.class));

        } else if (item == LeftMenuAdapter.LeftMenuItem.LIFE_COUNTER) {
            startActivity(new Intent(this, LifeCounterActivity.class));

        } else if (item == LeftMenuAdapter.LeftMenuItem.ABOUT) {
            openDialog(DBDialog.ABOUT);

        } else if (item == LeftMenuAdapter.LeftMenuItem.CREATE_DB) {
            // NB: WARNING, FOR RECREATE DATABASE
            String packageName = getApplication().getPackageName();
            new CreateDBAsyncTask(this, packageName).execute();

        } else if (item == LeftMenuAdapter.LeftMenuItem.FORCE_CRASH) {
            throw new RuntimeException("This is a crash");
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onBackPressed() {
        if (setList.getHeight() > 0) {
            showHideSetList(false);
        } else {
            super.onBackPressed();
        }
    }
}
