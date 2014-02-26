package com.dbottillo.mtgsearch;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
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

        sets = new ArrayList<MTGSet>();
        setAdapter = new MTGSetSpinnerAdapter(this, sets);

        showLoadingInActionBar();
        new DBAsyncTask(this, this, DBAsyncTask.TASK_SET_LIST).execute();

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
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(sets.get(position)))
                .commit();
        return true;
    }


    @Override
    public void onTaskFinished(ArrayList<?> result) {
        sets.clear();
        for (Object set : result){
            sets.add((MTGSet) set);
        }
        setAdapter.notifyDataSetChanged();
        result.clear();
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
}
