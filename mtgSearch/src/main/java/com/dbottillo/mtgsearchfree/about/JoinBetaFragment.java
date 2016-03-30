package com.dbottillo.mtgsearchfree.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.base.DBFragment;
import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.persistence.GeneralPreferences;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.net.Uri.parse;

public class JoinBetaFragment extends DBFragment implements View.OnClickListener{

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
