package com.dbottillo.mtgsearchfree.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.view.fragments.MTGSetFragment;

public class SearchFragment extends MTGSetFragment {

    private static final String SEARCH_PARAMS = "searchParams";

    public static SearchFragment newInstance(SearchParams searchParams) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(SEARCH_PARAMS, searchParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set, container, false);

        setupSetFragment(rootView, (SearchParams) getArguments().getParcelable(SEARCH_PARAMS));

        return rootView;
    }

}
