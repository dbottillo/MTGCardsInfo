package com.dbottillo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.dbottillo.R;
import com.dbottillo.adapters.SearchSetAdapter;
import com.dbottillo.resources.MTGSet;
import com.dbottillo.search.IntParam;
import com.dbottillo.search.SearchParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MTGSearchView extends RelativeLayout {

    String[] operators = new String[]{"=", ">", "<", ">=", "<="};
    ArrayList<MTGSet> sets;

    @Bind(R.id.search_name)
    AppCompatEditText name;
    @Bind(R.id.search_types)
    AppCompatEditText types;
    @Bind(R.id.search_cmc)
    AppCompatEditText cmc;
    @Bind(R.id.search_power)
    AppCompatEditText power;
    @Bind(R.id.search_tough)
    AppCompatEditText tough;
    @Bind(R.id.search_power_operator)
    Spinner powerOp;
    @Bind(R.id.search_toughness_operator)
    Spinner toughOp;
    @Bind(R.id.search_cmc_operator)
    Spinner cmcOp;
    @Bind(R.id.search_w)
    AppCompatCheckBox white;
    @Bind(R.id.search_u)
    AppCompatCheckBox blue;
    @Bind(R.id.search_b)
    AppCompatCheckBox black;
    @Bind(R.id.search_r)
    AppCompatCheckBox red;
    @Bind(R.id.search_g)
    AppCompatCheckBox green;
    @Bind(R.id.search_m)
    AppCompatCheckBox multi;
    @Bind(R.id.search_nm)
    AppCompatCheckBox noMulti;
    @Bind(R.id.search_common)
    AppCompatCheckBox common;
    @Bind(R.id.search_uncommon)
    AppCompatCheckBox uncommon;
    @Bind(R.id.search_rare)
    AppCompatCheckBox rare;
    @Bind(R.id.search_mythic)
    AppCompatCheckBox mythic;
    @Bind(R.id.search_set)
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

        ArrayAdapter<CharSequence> cmcAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, operators);
        cmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmcOp.setAdapter(cmcAdapter);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, operators);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        powerOp.setAdapter(adapter);

        ArrayAdapter<CharSequence> toughAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, operators);
        toughAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toughOp.setAdapter(toughAdapter);

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
        searchParams.setName(name.getText().toString()).setTypes(types.getText().toString());
        searchParams.setCmc(new IntParam(operators[cmcOp.getSelectedItemPosition()], parseNumber(cmc.getText().toString())));
        searchParams.setPower(new IntParam(operators[powerOp.getSelectedItemPosition()], parseNumber(power.getText().toString())));
        searchParams.setTough(new IntParam(operators[toughOp.getSelectedItemPosition()], parseNumber(tough.getText().toString())));
        searchParams.setWhite(white.isChecked());
        searchParams.setBlue(blue.isChecked());
        searchParams.setBlack(black.isChecked());
        searchParams.setRed(red.isChecked());
        searchParams.setGreen(green.isChecked());
        searchParams.setMulti(multi.isChecked());
        searchParams.setNomulti(noMulti.isChecked());
        searchParams.setCommon(common.isChecked());
        searchParams.setUncommon(uncommon.isChecked());
        searchParams.setRare(rare.isChecked());
        searchParams.setMythic(mythic.isChecked());
        searchParams.setSetId(sets.get(set.getSelectedItemPosition()).getId());
        return searchParams;
    }

    private static int parseNumber(String text) {
        if (text.length() == 0) {
            return -1;
        }
        return Integer.parseInt(text);
    }

    public void refreshSets(ArrayList<MTGSet> sets) {
        this.sets.addAll(sets);
        searchSetAdapter.notifyDataSetChanged();
    }
}
