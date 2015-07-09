package com.dbottillo.search;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.R;
import com.dbottillo.cards.MTGSetFragment;
import com.dbottillo.filter.FilterActivity;
import com.dbottillo.view.SlidingUpPanelLayout;

public class SearchFragment extends MTGSetFragment implements SlidingUpPanelLayout.PanelSlideListener {

    private static final String SEARCH = "search";

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((FilterActivity) activity).addPanelSlideListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        setupSetFragment(rootView, true, getArguments().getString(SEARCH));

        return rootView;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelCollapsed(View panel) {
        updateSetFragment();
    }

    @Override
    public void onPanelExpanded(View panel) {
    }

    @Override
    public void onPanelAnchored(View panel) {
    }
}
