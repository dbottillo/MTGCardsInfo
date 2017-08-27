package com.dbottillo.mtgsearchfree.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;

public class FilterPickerView extends LinearLayout implements View.OnClickListener {

    public interface OnFilterPickerListener {
        void filterUpdated(CardFilter.TYPE type, boolean on);
    }

    View filterPanelContainer;
    TextView filterTitle;
    View filterDivisor;

    ToggleButton toggleW;
    ToggleButton toggleU;
    ToggleButton toggleB;
    ToggleButton toggleR;
    ToggleButton toggleG;

    ToggleButton toggleLand;
    ToggleButton toggleArtifact;
    ToggleButton toggleEldrazi;

    ToggleButton toggleCommon;
    ToggleButton toggleUncommon;
    ToggleButton toggleRare;
    ToggleButton toggleMythic;

    ToggleButton toggleOrder;
    TextView orderTitle;
    View orderDivisor;

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
        View view = inflater.inflate(R.layout.view_filter_picker, this);

        filterPanelContainer = view.findViewById(R.id.filter_panel_container);
        filterTitle = view.findViewById(R.id.filter_title);
        filterDivisor = view.findViewById(R.id.filter_divisor);

        toggleW = view.findViewById(R.id.toggle_white);
        toggleU = view.findViewById(R.id.toggle_blue);
        toggleB = view.findViewById(R.id.toggle_black);
        toggleR = view.findViewById(R.id.toggle_red);
        toggleG = view.findViewById(R.id.toggle_green);

        toggleLand = view.findViewById(R.id.toggle_land);
        toggleArtifact = view.findViewById(R.id.toggle_artifact);
        toggleEldrazi = view.findViewById(R.id.toggle_eldrazi);

        toggleCommon = view.findViewById(R.id.toggle_common);
        toggleUncommon = view.findViewById(R.id.toggle_uncommon);
        toggleRare = view.findViewById(R.id.toggle_rare);
        toggleMythic = view.findViewById(R.id.toggle_myhtic);

        toggleOrder = view.findViewById(R.id.toggle_order);
        orderTitle = view.findViewById(R.id.order_title);
        orderDivisor = view.findViewById(R.id.order_divisor);

        toggleW.setOnClickListener(this);
        toggleU.setOnClickListener(this);
        toggleR.setOnClickListener(this);
        toggleG.setOnClickListener(this);
        toggleB.setOnClickListener(this);

        toggleLand.setOnClickListener(this);
        toggleArtifact.setOnClickListener(this);
        toggleEldrazi.setOnClickListener(this);

        toggleCommon.setOnClickListener(this);
        toggleUncommon.setOnClickListener(this);
        toggleRare.setOnClickListener(this);
        toggleMythic.setOnClickListener(this);

        toggleOrder.setOnClickListener(this);
    }

    public void setFilterPickerListener(OnFilterPickerListener list) {
        listener = list;
    }

    public void refresh(CardFilter filter) {
        LOG.d();
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
        toggleOrder.setChecked(filter.sortWUBGR);
    }

    @Override
    public void onClick(View view) {
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
            case R.id.toggle_order:
                listener.filterUpdated(CardFilter.TYPE.SORT_WUBGR, on);
                break;
            default:
                break;
        }
    }

    public void configure(boolean showFilter, boolean showOrder) {
        filterPanelContainer.setVisibility(showFilter ? View.VISIBLE : View.GONE);
        filterTitle.setVisibility(showFilter ? View.VISIBLE : View.GONE);
        filterDivisor.setVisibility(showFilter ? View.VISIBLE : View.GONE);

        toggleOrder.setVisibility(showOrder ? View.VISIBLE : View.GONE);
        orderDivisor.setVisibility(showOrder ? View.VISIBLE : View.GONE);
        orderTitle.setVisibility(showOrder ? View.VISIBLE : View.GONE);
    }
}

