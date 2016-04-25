package com.dbottillo.mtgsearchfree.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;

import butterknife.ButterKnife;

public class JoinBetaFragment extends BasicFragment implements View.OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        ButterKnife.bind(this, v);

        setActionBarTitle(getString(R.string.action_about));

        return v;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getPageTrack() {
        return "/about";
    }


}
