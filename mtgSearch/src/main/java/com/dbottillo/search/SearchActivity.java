package com.dbottillo.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.dbottillo.R;
import com.dbottillo.base.MTGApp;
import com.dbottillo.common.FilterActivity;
import com.dbottillo.common.MTGSetFragment;
import com.dbottillo.helper.LOG;
import com.dbottillo.view.SlidingUpPanelLayout;

public class SearchActivity extends FilterActivity implements SlidingUpPanelLayout.PanelSlideListener, SearchView.OnQueryTextListener {

    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.action_search);

        setupSlidingPanel(this);
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
        getApp().trackEvent(MTGApp.UA_CATEGORY_UI, "panel", "collapsed");
        MTGSetFragment setFragment = (MTGSetFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (setFragment != null) {
            setFragment.refreshUI();
        }

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

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(this);

        try {
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText textView = (EditText) searchView.findViewById(id);
            textView.setHint(R.string.search_hint);
            textView.setHintTextColor(getResources().getColor(R.color.light_grey));
        } catch (Exception e) {
            LOG.e(e.getLocalizedMessage());
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
        getApp().trackEvent(MTGApp.UA_CATEGORY_SEARCH, "done", query);
        if (query.length() < 3) {
            Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
            return true;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MTGSetFragment.newInstance(query))
                .commit();
        collapseSlidingPanel();
        hideIme();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
