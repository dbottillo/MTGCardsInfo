package com.dbottillo.mtgsearchfree.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.base.DBFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class JoinBetaFragment extends DBFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_join_beta, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(getString(R.string.join_beta_action));
        return v;
    }

    @OnClick(R.id.join_action)
    public void onClick(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps/testing/com.dbottillo.mtgsearchfree")));
    }

    @Override
    public String getPageTrack() {
        return "/join_beta";
    }

}
