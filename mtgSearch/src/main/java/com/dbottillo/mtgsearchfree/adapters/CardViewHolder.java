package com.dbottillo.mtgsearchfree.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;

public class CardViewHolder extends RecyclerView.ViewHolder {

    View parent;
    TextView name;
    TextView setName;
    TextView rarity;
    TextView cost;
    View indicator;
    ImageButton more;

    public CardViewHolder(View row) {
        super(row);
        parent = row.findViewById(R.id.card_parent);
        name = (TextView) row.findViewById(R.id.card_name);
        setName = (TextView) row.findViewById(R.id.card_set_name);
        rarity = (TextView) row.findViewById(R.id.card_rarity);
        cost = (TextView) row.findViewById(R.id.card_cost);
        indicator = row.findViewById(R.id.card_indicator);
        more = (ImageButton) row.findViewById(R.id.card_more_option);
    }
}
