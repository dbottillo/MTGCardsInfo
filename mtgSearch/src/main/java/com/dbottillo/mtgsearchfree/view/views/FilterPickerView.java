package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterPickerView extends LinearLayout {

    public interface OnFilterPickerListener {
        void filterUpdated(CardFilter.TYPE type, boolean on);
    }

    @BindView(R.id.filter_panel_container)
    View filterPanelContainer;
    @BindView(R.id.filter_title)
    TextView filterTitle;
    @BindView(R.id.filter_divisor)
    View filterDivisor;

    @BindView(R.id.toggle_white)
    ToggleButton toggleW;
    @BindView(R.id.toggle_blue)
    ToggleButton toggleU;
    @BindView(R.id.toggle_black)
    ToggleButton toggleB;
    @BindView(R.id.toggle_red)
    ToggleButton toggleR;
    @BindView(R.id.toggle_green)
    ToggleButton toggleG;

    @BindView(R.id.toggle_land)
    ToggleButton toggleLand;
    @BindView(R.id.toggle_artifact)
    ToggleButton toggleArtifact;
    @BindView(R.id.toggle_eldrazi)
    ToggleButton toggleEldrazi;

    @BindView(R.id.toggle_common)
    ToggleButton toggleCommon;
    @BindView(R.id.toggle_uncommon)
    ToggleButton toggleUncommon;
    @BindView(R.id.toggle_rare)
    ToggleButton toggleRare;
    @BindView(R.id.toggle_myhtic)
    ToggleButton toggleMythic;

    @BindView(R.id.toggle_order)
    ToggleButton toggleOrder;
    @BindView(R.id.order_title)
    TextView orderTitle;
    @BindView(R.id.order_divisor)
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

        ButterKnife.bind(this, view);
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
    }

    @OnClick({R.id.toggle_white, R.id.toggle_blue, R.id.toggle_black, R.id.toggle_red,
            R.id.toggle_green, R.id.toggle_artifact, R.id.toggle_land, R.id.toggle_eldrazi,
            R.id.toggle_common, R.id.toggle_uncommon,
            R.id.toggle_rare, R.id.toggle_myhtic, R.id.toggle_order})
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

