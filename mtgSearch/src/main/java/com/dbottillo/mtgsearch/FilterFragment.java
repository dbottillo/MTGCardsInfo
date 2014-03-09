package com.dbottillo.mtgsearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Picasso;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class FilterFragment extends DBFragment {

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

        switch (view.getId()) {
            case R.id.toggle_white:
                editor.putBoolean(FilterHelper.FILTER_WHITE, on);
                break;
            case R.id.toggle_blue:
                editor.putBoolean(FilterHelper.FILTER_BLUE, on);
                break;
            case R.id.toggle_black:
                editor.putBoolean(FilterHelper.FILTER_BLACK, on);
                break;
            case R.id.toggle_red:
                editor.putBoolean(FilterHelper.FILTER_RED, on);
                break;
            case R.id.toggle_green:
                editor.putBoolean(FilterHelper.FILTER_GREEN, on);
                break;
            case R.id.toggle_artifact:
                editor.putBoolean(FilterHelper.FILTER_ARTIFACT, on);
                break;
            case R.id.toggle_land:
                editor.putBoolean(FilterHelper.FILTER_LAND, on);
                break;
            case R.id.toggle_common:
                editor.putBoolean(FilterHelper.FILTER_COMMON, on);
                break;
            case R.id.toggle_uncommon:
                editor.putBoolean(FilterHelper.FILTER_UNCOMMON, on);
                break;
            case R.id.toggle_rare:
                editor.putBoolean(FilterHelper.FILTER_RARE, on);
                break;
            case R.id.toggle_myhtic:
                editor.putBoolean(FilterHelper.FILTER_MYHTIC, on);
                break;
            default: {

            }
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
}
