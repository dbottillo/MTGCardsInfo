package com.dbottillo.mtgsearch;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.adapters.MTGSetSpinnerAdapter;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.view.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.logging.Filter;

public class MainActivity extends DBActivity implements ActionBar.OnNavigationListener, DBAsyncTask.DBAsyncTaskListener, SlidingUpPanelLayout.PanelSlideListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private ArrayList<MTGSet> sets;
    private MTGSetSpinnerAdapter setAdapter;

    private SlidingUpPanelLayout slidingPanel;

    private FilterFragment filterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setPanelSlideListener(this);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        if (savedInstanceState == null){
            sets = new ArrayList<MTGSet>();

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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
        outState.putParcelableArrayList("SET", sets);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt("setPosition", position);
        editor.commit();
        loadSet();
        return true;
    }

    private void loadSet(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(sets.get(getSharedPreferences().getInt("setPosition",0))))
                .commit();
        slidingPanel.collapsePane();
    }

    @Override
    public void onTaskFinished(ArrayList<?> result) {
        sets.clear();
        for (Object set : result){
            sets.add((MTGSet) set);
        }
        setAdapter.notifyDataSetChanged();
        result.clear();

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

    }

    @Override
    public void onPanelCollapsed(View panel) {
        MTGSetFragment setFragment = (MTGSetFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        setFragment.refreshUI();
    }

    @Override
    public void onPanelExpanded(View panel) {

    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    public void onToggleClicked(View view) {
        filterFragment.onToggleClicked(view);
    }

    @Override
    public void onBackPressed(){
        if (slidingPanel.isExpanded()){
            slidingPanel.collapsePane();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                AboutFragment newFragment = new AboutFragment();
                newFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
