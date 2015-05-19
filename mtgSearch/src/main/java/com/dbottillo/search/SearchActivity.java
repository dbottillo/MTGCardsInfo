package com.dbottillo.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.cards.MTGSetFragment;
import com.dbottillo.filter.FilterActivity;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.view.SlidingUpPanelLayout;

public class SearchActivity extends FilterActivity implements SlidingUpPanelLayout.PanelSlideListener, SearchView.OnQueryTextListener {

    SearchView searchView;
    EditText searchEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupToolbar();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_search);

        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
        }

        setupSlidingPanel(this);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            handleIntent(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            setIntent(intent);
            handleIntent(intent);
        }
    }

    private boolean pendingSearch = false;
    private String query = "";

    private void handleIntent(Intent intent) {
        pendingSearch = true;
        query = intent.getStringExtra(SearchManager.QUERY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (pendingSearch) {
            doSearch();
            pendingSearch = false;
        }
    }

    @Override
    public String getPageTrack() {
        return "/search_main";
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        setRotationArrow(180 - (180 * slideOffset));
    }

    @Override
    public void onPanelCollapsed(View panel) {
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "collapsed");
        MTGSetFragment setFragment = (MTGSetFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (setFragment != null) {
            setFragment.refreshUI();
        }

        setRotationArrow(0);
    }

    @Override
    public void onPanelExpanded(View panel) {
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_UI, "panel", "expanded");
        setRotationArrow(180);

    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        //searchView = (SearchView) searchItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        //searchView.setOnQueryTextListener(this);

        try {
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            searchEditText = (EditText) searchView.findViewById(id);
            searchEditText.setText(query);
            searchEditText.setHintTextColor(getResources().getColor(R.color.light_grey));
        } catch (Exception e) {
            //LOG.e(e.getLocalizedMessage());
        }

        searchView.requestFocus();

        /*MenuItemCompat.setOnActionExpandListener(searchItem,
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
                });*/


        /*// Associate searchable configuration with the SearchView
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        if (mSearchViewHasFocus) {
            searchView.requestFocus();
        } else {
            searchView.clearFocus();
            hideIme();
        }

        final EditText searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(getResources().getColor(R.color.action_bar_title_color));
        searchText.setHintTextColor(getResources().getColor(R.color.dusty_grey));

        setupMediaRouteView(menu);*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        doSearch();
        return true;
    }

    private void doSearch() {
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_SEARCH, "done", query);
        if (query.length() < 3) {
            Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
            return;
        }
        if (searchEditText != null) {
            searchEditText.setText(query);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(query))
                .commit();
        collapseSlidingPanel();
        hideIme();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
