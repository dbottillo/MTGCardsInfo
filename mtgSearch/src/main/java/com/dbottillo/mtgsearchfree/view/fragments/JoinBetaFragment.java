package com.dbottillo.mtgsearchfree.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinBetaFragment extends BasicFragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_join_beta, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(getString(R.string.action_join_beta));
        return v;
    }

    @OnClick(R.id.join_action)
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps/testing/com.dbottillo.mtgsearchfree"));
        startActivity(intent);
    }

    @Override
    public String getPageTrack() {
        return "/join_beta";
    }


}
