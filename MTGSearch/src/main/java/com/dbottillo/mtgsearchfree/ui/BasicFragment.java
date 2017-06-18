package com.dbottillo.mtgsearchfree.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.ui.BasicActivity;

import butterknife.ButterKnife;

public abstract class BasicFragment extends DialogFragment {

    protected BasicActivity dbActivity;
    protected boolean isPortrait = false;
    protected MTGApp app;
    public Toolbar toolbar;
    public TextView toolbarTitle;
    protected int heightToolbar;

    public static final String PREF_SHOW_IMAGE = "show_image";
    public static final String PREF_SCREEN_ON = "screen_on";
    public static final String PREF_TWO_HG_ENABLED = "two_hg";
    public static final String PREF_SORT_WUBRG = "sort_wubrg";


    public void onAttach(Context context) {
        super.onAttach(context);
        LOG.d();

        this.dbActivity = (BasicActivity) context;
        app = (MTGApp) dbActivity.getApplication();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (heightToolbar <= 0) {
            final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.actionBarSize});
            heightToolbar = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    protected MTGApp getMTGApp() {
        return dbActivity.getMtgApp();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG.d();

        setHasOptionsMenu(true);
    }

    protected void setActionBarTitle(String title) {
        if (dbActivity.getSupportActionBar() != null) {
            dbActivity.getSupportActionBar().setTitle(title);
        }
    }

    public void onResume() {
        super.onResume();
        LOG.d();
        TrackingManager.trackPage(getPageTrack());
    }

    public abstract String getPageTrack();


    public boolean onBackPressed() {
        return false;
    }

    public void setupToolbar(View rootView) {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbar.setTitle("");
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setTitle(getTitle());
    }

    public void setTitle(final String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    public String getTitle() {
        return "";
    }

}

