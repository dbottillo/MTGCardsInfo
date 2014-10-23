package com.dbottillo.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.helper.FilterHelper;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class FilterFragment extends DBFragment implements View.OnClickListener {

    public FilterFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FilterActivity filterActivity = (FilterActivity) getActivity();
        filterActivity.getSlidingPanel().setDragView(getView().findViewById(R.id.filter_draggable));

        if (!getApp().isPremium()) {
            createAdView("ca-app-pub-8119815713373556/7301149617");

            FrameLayout layout = (FrameLayout) getView().findViewById(R.id.ad_container);
            layout.addView(getAdView());

            getAdView().loadAd(createAdRequest());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        Button applyFilter = (Button) rootView.findViewById(R.id.btn_apply_filter);
        applyFilter.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateFilterUI();
    }


    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        String label = "";

        int i = view.getId();
        if (i == R.id.toggle_white) {
            editor.putBoolean(FilterHelper.FILTER_WHITE, on);
            label = "white";
        } else if (i == R.id.toggle_blue) {
            editor.putBoolean(FilterHelper.FILTER_BLUE, on);
            label = "blue";
        } else if (i == R.id.toggle_black) {
            editor.putBoolean(FilterHelper.FILTER_BLACK, on);
            label = "black";
        } else if (i == R.id.toggle_red) {
            editor.putBoolean(FilterHelper.FILTER_RED, on);
            label = "red";
        } else if (i == R.id.toggle_green) {
            editor.putBoolean(FilterHelper.FILTER_GREEN, on);
            label = "green";
        } else if (i == R.id.toggle_artifact) {
            editor.putBoolean(FilterHelper.FILTER_ARTIFACT, on);
            label = "artifact";
        } else if (i == R.id.toggle_land) {
            editor.putBoolean(FilterHelper.FILTER_LAND, on);
            label = "land";
        } else if (i == R.id.toggle_common) {
            editor.putBoolean(FilterHelper.FILTER_COMMON, on);
            label = "common";
        } else if (i == R.id.toggle_uncommon) {
            editor.putBoolean(FilterHelper.FILTER_UNCOMMON, on);
            label = "uncommon";
        } else if (i == R.id.toggle_rare) {
            editor.putBoolean(FilterHelper.FILTER_RARE, on);
            label = "rare";
        } else if (i == R.id.toggle_myhtic) {
            editor.putBoolean(FilterHelper.FILTER_MYHTIC, on);
            label = "mythic";
        } else {
        }

        getApp().trackEvent(MTGApp.UA_CATEGORY_FILTER, MTGApp.UA_ACTION_TOGGLE, label);

        editor.commit();

        updateFilterUI();
    }

    public void updateFilterUI() {
        trackEvent(MTGApp.UA_CATEGORY_FILTER, "update", "");
        String filterString = "";

        String colorActive = "#FFF";
        String colorInactive = "#CCC";

        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_WHITE, true), "W", "#FFFFFF");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLUE, true), "U", "#0044FF");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLACK, true), "B", "#000000");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_RED, true), "R", "#FF0000");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_GREEN, true), "G", "#7ED321");

        filterString += " - ";
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_ARTIFACT, true), "A", "#CCCCCC");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_LAND, true), "L", "#E6FF00");
        filterString += "  - ";

        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true), "C", "#000000");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_UNCOMMON, true), "U", "#AAAAAA");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_RARE, true), "R", "#BD9723");
        filterString += addEntryFilterString(getSharedPreferences().getBoolean(FilterHelper.FILTER_MYHTIC, true), "M", "#D46805");

        ((ToggleButton) getView().findViewById(R.id.toggle_white)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_WHITE, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_blue)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLUE, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_black)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLACK, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_red)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_RED, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_green)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_GREEN, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_artifact)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_ARTIFACT, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_land)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_LAND, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_common)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_uncommon)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_UNCOMMON, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_rare)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_RARE, true));
        ((ToggleButton) getView().findViewById(R.id.toggle_myhtic)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_MYHTIC, true));

        TextView textFilter = (TextView) getView().findViewById(R.id.filter_text);
        textFilter.setText(Html.fromHtml(filterString));
    }

    public String addEntryFilterString(boolean active, String text, String colorActive) {
        String filterString = "";
        if (active) {
            filterString += "<font color=\"" + colorActive + "\">" + text + "</font>";
        } else {
            filterString += "<font color=\"#12212F\">" + text + "</font>";
        }
        filterString += "&nbsp;";
        return filterString;
    }

    @Override
    public void onClick(View v) {
        trackEvent(MTGApp.UA_CATEGORY_FILTER, MTGApp.UA_ACTION_CLOSE, "");
        FilterActivity filterActivity = (FilterActivity) getActivity();
        filterActivity.getSlidingPanel().collapsePane();
    }

    @Override
    public String getPageTrack() {
        return "/filter";
    }
}
