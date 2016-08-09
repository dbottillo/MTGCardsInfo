package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.IntParam;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.adapters.SearchSetAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MTGSearchView extends RelativeLayout {

    String[] operators = new String[]{"=", ">", "<", ">=", "<="};
    ArrayList<MTGSet> sets;

    @BindView(R.id.search_name)
    AppCompatEditText name;
    @BindView(R.id.search_types)
    AppCompatEditText types;
    @BindView(R.id.search_text)
    AppCompatEditText text;
    @BindView(R.id.search_cmc)
    AppCompatEditText cmc;
    @BindView(R.id.search_power)
    AppCompatEditText power;
    @BindView(R.id.search_tough)
    AppCompatEditText tough;
    @BindView(R.id.search_power_operator)
    Spinner powerOp;
    @BindView(R.id.search_toughness_operator)
    Spinner toughOp;
    @BindView(R.id.search_cmc_operator)
    Spinner cmcOp;
    @BindView(R.id.search_w)
    AppCompatCheckBox white;
    @BindView(R.id.search_u)
    AppCompatCheckBox blue;
    @BindView(R.id.search_b)
    AppCompatCheckBox black;
    @BindView(R.id.search_r)
    AppCompatCheckBox red;
    @BindView(R.id.search_g)
    AppCompatCheckBox green;
    @BindView(R.id.search_m)
    AppCompatCheckBox multi;
    @BindView(R.id.search_nm)
    AppCompatCheckBox noMulti;
    @BindView(R.id.search_l)
    AppCompatCheckBox land;
    @BindView(R.id.search_common)
    AppCompatCheckBox common;
    @BindView(R.id.search_uncommon)
    AppCompatCheckBox uncommon;
    @BindView(R.id.search_rare)
    AppCompatCheckBox rare;
    @BindView(R.id.search_mythic)
    AppCompatCheckBox mythic;
    @BindView(R.id.search_set)
    Spinner set;

    private SearchSetAdapter searchSetAdapter;

    public MTGSearchView(Context context) {
        this(context, null);
    }

    public MTGSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MTGSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.search_form_view, this);
        ButterKnife.bind(this);

        sets = new ArrayList<>();
        sets.add(new MTGSet(-1, getResources().getString(R.string.search_set_all)));
        sets.add(new MTGSet(-2, getResources().getString(R.string.search_set_standard)));

        searchSetAdapter = new SearchSetAdapter(getContext(), sets);
        set.setAdapter(searchSetAdapter);

        ArrayAdapter<CharSequence> cmcAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.row_spinner_item, operators);
        cmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmcOp.setAdapter(cmcAdapter);
        cmcOp.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.row_spinner_item, operators);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        powerOp.setAdapter(adapter);
        powerOp.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<CharSequence> toughAdapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.row_spinner_item, operators);
        toughAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toughOp.setAdapter(toughAdapter);
        toughOp.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        multi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    noMulti.setChecked(false);
                }
            }
        });
        noMulti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    multi.setChecked(false);
                }
            }
        });
    }

    public SearchParams getSearchParams() {
        SearchParams searchParams = new SearchParams();
        searchParams.setName(name.getText().toString());
        searchParams.setTypes(types.getText().toString());
        searchParams.setText(text.getText().toString());
        if (cmc.getText().length() > 0) {
            searchParams.setCmc(new IntParam(operators[cmcOp.getSelectedItemPosition()], Integer.parseInt(cmc.getText().toString())));
        }
        if (power.getText().length() > 0) {
            searchParams.setPower(new IntParam(operators[powerOp.getSelectedItemPosition()], Integer.parseInt(power.getText().toString())));
        }
        if (tough.getText().length() > 0) {
            searchParams.setTough(new IntParam(operators[toughOp.getSelectedItemPosition()], Integer.parseInt(tough.getText().toString())));
        }
        searchParams.setWhite(white.isChecked());
        searchParams.setBlue(blue.isChecked());
        searchParams.setBlack(black.isChecked());
        searchParams.setRed(red.isChecked());
        searchParams.setGreen(green.isChecked());
        searchParams.setOnlyMulti(multi.isChecked());
        searchParams.setNoMulti(noMulti.isChecked());
        searchParams.setLand(land.isChecked());
        searchParams.setCommon(common.isChecked());
        searchParams.setUncommon(uncommon.isChecked());
        searchParams.setRare(rare.isChecked());
        searchParams.setMythic(mythic.isChecked());
        searchParams.setSetId(sets.get(set.getSelectedItemPosition()).getId());
        return searchParams;
    }

    public void refreshSets(List<MTGSet> sets) {
        LOG.d();
        this.sets.addAll(sets);
        searchSetAdapter.notifyDataSetChanged();
    }
}
