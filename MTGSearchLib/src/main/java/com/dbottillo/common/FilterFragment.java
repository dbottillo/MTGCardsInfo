package com.dbottillo.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.helper.FilterHelper;
import com.dbottillo.mtgsearch.R;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class FilterFragment extends DBFragment implements View.OnClickListener {

    public FilterFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity main = (MainActivity) getActivity();
        main.getSlidingPanel().setDragView(getView().findViewById(R.id.filter_draggable));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        Button applyFilter = (Button) rootView.findViewById(R.id.btn_apply_filter);
        applyFilter.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        updateFilterUI();
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        int i = view.getId();
        if (i == R.id.toggle_white) {
            editor.putBoolean(FilterHelper.FILTER_WHITE, on);

        } else if (i == R.id.toggle_blue) {
            editor.putBoolean(FilterHelper.FILTER_BLUE, on);

        } else if (i == R.id.toggle_black) {
            editor.putBoolean(FilterHelper.FILTER_BLACK, on);

        } else if (i == R.id.toggle_red) {
            editor.putBoolean(FilterHelper.FILTER_RED, on);

        } else if (i == R.id.toggle_green) {
            editor.putBoolean(FilterHelper.FILTER_GREEN, on);

        } else if (i == R.id.toggle_artifact) {
            editor.putBoolean(FilterHelper.FILTER_ARTIFACT, on);

        } else if (i == R.id.toggle_land) {
            editor.putBoolean(FilterHelper.FILTER_LAND, on);

        } else if (i == R.id.toggle_common) {
            editor.putBoolean(FilterHelper.FILTER_COMMON, on);

        } else if (i == R.id.toggle_uncommon) {
            editor.putBoolean(FilterHelper.FILTER_UNCOMMON, on);

        } else if (i == R.id.toggle_rare) {
            editor.putBoolean(FilterHelper.FILTER_RARE, on);

        } else if (i == R.id.toggle_myhtic) {
            editor.putBoolean(FilterHelper.FILTER_MYHTIC, on);

        } else {
        }

        editor.commit();

        updateFilterUI();
    }

    private void updateFilterUI() {
        String filterString = "";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_WHITE, true)) filterString += "W";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_BLUE, true)) filterString += "B";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_BLACK, true)) filterString += "B";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_RED, true)) filterString += "R";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_GREEN, true)) filterString += "G";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_ARTIFACT, true)) filterString += "A";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_LAND, true)) filterString += "L";
        filterString +=" - ";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true)) filterString += "C";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_UNCOMMON, true)) filterString += "U";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_RARE, true)) filterString += "R";
        if (getSharedPreferences().getBoolean(FilterHelper.FILTER_MYHTIC, true)) filterString += "M";

        ((ToggleButton)getView().findViewById(R.id.toggle_white)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_WHITE, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_blue)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLUE, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_black)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_BLACK, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_red)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_RED, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_green)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_GREEN, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_artifact)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_ARTIFACT, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_land)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_LAND, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_common)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_COMMON, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_uncommon)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_UNCOMMON, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_rare)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_RARE, true));
        ((ToggleButton)getView().findViewById(R.id.toggle_myhtic)).setChecked(getSharedPreferences().getBoolean(FilterHelper.FILTER_MYHTIC, true));

        TextView textFilter = (TextView) getView().findViewById(R.id.filter_text);
        textFilter.setText(getString(R.string.filter)+": "+filterString);
    }

    @Override
    public void onClick(View v) {
        MainActivity main = (MainActivity) getActivity();
        main.getSlidingPanel().collapsePane();
    }
}
