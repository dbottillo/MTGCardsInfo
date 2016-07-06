package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterPickerView extends LinearLayout {

    public interface OnFilterPickerListener {
        void filterUpdated(CardFilter.TYPE type, boolean on);
    }

    @Bind(R.id.filter_text)
    TextView filterText;
    @Bind(R.id.arrow_filter)
    ImageView arrow;
    @Bind(R.id.toggle_white)
    ToggleButton toggleW;
    @Bind(R.id.toggle_blue)
    ToggleButton toggleU;
    @Bind(R.id.toggle_black)
    ToggleButton toggleB;
    @Bind(R.id.toggle_red)
    ToggleButton toggleR;
    @Bind(R.id.toggle_green)
    ToggleButton toggleG;

    @Bind(R.id.toggle_land)
    ToggleButton toggleLand;
    @Bind(R.id.toggle_artifact)
    ToggleButton toggleArtifact;
    @Bind(R.id.toggle_eldrazi)
    ToggleButton toggleEldrazi;

    @Bind(R.id.toggle_common)
    ToggleButton toggleCommon;
    @Bind(R.id.toggle_uncommon)
    ToggleButton toggleUncommon;
    @Bind(R.id.toggle_rare)
    ToggleButton toggleRare;
    @Bind(R.id.toggle_myhtic)
    ToggleButton toggleMythic;

    private OnFilterPickerListener listener;

    public FilterPickerView(Context ctx) {
        this(ctx, null);
    }

    public FilterPickerView(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, -1);
    }

    public FilterPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_filter_picker, this, true);

        ButterKnife.bind(this, view);
    }

    public void setFilterPickerListener(OnFilterPickerListener list) {
        listener = list;
    }

    private void setRotationArrow(float angle) {
        arrow.setRotation(angle);
    }

    public void refresh(CardFilter filter) {
        LOG.d();
        String filterString = "";

        filterString += addEntryFilterString(filter.white, "W");
        filterString += addEntryFilterString(filter.blue, "U");
        filterString += addEntryFilterString(filter.black, "B");
        filterString += addEntryFilterString(filter.red, "R");
        filterString += addEntryFilterString(filter.green, "G");

        filterString += " - ";
        filterString += addEntryFilterString(filter.eldrazi, "A");
        filterString += addEntryFilterString(filter.land, "L");
        filterString += addEntryFilterString(filter.eldrazi, "E");
        filterString += " - ";

        filterString += addEntryFilterString(filter.common, "C");
        filterString += addEntryFilterString(filter.uncommon, "U");
        filterString += addEntryFilterString(filter.rare, "R");
        filterString += addEntryFilterString(filter.mythic, "M");

        toggleW.setChecked(filter.white);
        toggleU.setChecked(filter.blue);
        toggleB.setChecked(filter.black);
        toggleR.setChecked(filter.red);
        toggleG.setChecked(filter.green);
        toggleArtifact.setChecked(filter.artifact);
        toggleLand.setChecked(filter.land);
        toggleEldrazi.setChecked(filter.eldrazi);
        toggleCommon.setChecked(filter.common);
        toggleUncommon.setChecked(filter.uncommon);
        toggleRare.setChecked(filter.rare);
        toggleMythic.setChecked(filter.mythic);

        filterText.setText(Html.fromHtml(filterString));
    }

    public String addEntryFilterString(boolean active, String text) {
        String filterString = "";
        if (active) {
            filterString += "<font color=\"#FFFFFF\">" + text + "</font>";
        } else {
            filterString += "<font color=\"#777777\">" + text + "</font>";
        }
        filterString += "&nbsp;";
        return filterString;
    }

    @OnClick({R.id.toggle_white, R.id.toggle_blue, R.id.toggle_black, R.id.toggle_red,
            R.id.toggle_green, R.id.toggle_artifact, R.id.toggle_land, R.id.toggle_eldrazi,
            R.id.toggle_common, R.id.toggle_uncommon,
            R.id.toggle_rare, R.id.toggle_myhtic})
    void onToggleClicked(View view) {
        LOG.d();
        boolean on = ((ToggleButton) view).isChecked();
        switch (view.getId()) {
            case R.id.toggle_white:
                listener.filterUpdated(CardFilter.TYPE.WHITE, on);
                break;
            case R.id.toggle_blue:
                listener.filterUpdated(CardFilter.TYPE.BLUE, on);
                break;
            case R.id.toggle_black:
                listener.filterUpdated(CardFilter.TYPE.BLACK, on);
                break;
            case R.id.toggle_red:
                listener.filterUpdated(CardFilter.TYPE.RED, on);
                break;
            case R.id.toggle_green:
                listener.filterUpdated(CardFilter.TYPE.GREEN, on);
                break;
            case R.id.toggle_artifact:
                listener.filterUpdated(CardFilter.TYPE.ARTIFACT, on);
                break;
            case R.id.toggle_land:
                listener.filterUpdated(CardFilter.TYPE.LAND, on);
                break;
            case R.id.toggle_eldrazi:
                listener.filterUpdated(CardFilter.TYPE.ELDRAZI, on);
                break;
            case R.id.toggle_common:
                listener.filterUpdated(CardFilter.TYPE.COMMON, on);
                break;
            case R.id.toggle_uncommon:
                listener.filterUpdated(CardFilter.TYPE.UNCOMMON, on);
                break;
            case R.id.toggle_rare:
                listener.filterUpdated(CardFilter.TYPE.RARE, on);
                break;
            case R.id.toggle_myhtic:
                listener.filterUpdated(CardFilter.TYPE.MYTHIC, on);
                break;
        }
    }

    public void onPanelSlide(float offset) {
        setRotationArrow(180 - (180 * offset));
    }

}

