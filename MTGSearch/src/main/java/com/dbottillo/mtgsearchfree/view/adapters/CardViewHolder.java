package com.dbottillo.mtgsearchfree.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.view.views.MTGLoader;

public class CardViewHolder extends RecyclerView.ViewHolder {

    View parent;
    TextView name;
    TextView setName;
    TextView rarity;
    TextView cost;
    View indicator;
    ImageButton more;
    ImageView image;
    MTGLoader loader;

    public CardViewHolder(View row, boolean grid) {
        super(row);
        parent = row.findViewById(R.id.card_parent);
        if (grid){
            image = (ImageView) row.findViewById(R.id.grid_item_card_image);
            loader = (MTGLoader) row.findViewById(R.id.grid_item_card_loader);
        } else {
            name = (TextView) row.findViewById(R.id.card_name);
            setName = (TextView) row.findViewById(R.id.card_set_name);
            rarity = (TextView) row.findViewById(R.id.card_rarity);
            cost = (TextView) row.findViewById(R.id.card_cost);
            indicator = row.findViewById(R.id.card_indicator);
            more = (ImageButton) row.findViewById(R.id.card_more_option);
        }
    }
}
