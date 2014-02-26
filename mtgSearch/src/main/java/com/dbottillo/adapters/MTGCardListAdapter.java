package com.dbottillo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbottillo.mtgsearch.R;
import com.dbottillo.resources.MTGCard;

import java.util.List;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardListAdapter extends BaseAdapter {

    private List<MTGCard> cards;
    private Context context;
    private LayoutInflater inflater;

    public MTGCardListAdapter(Context context, List<MTGCard> cards) {
        this.cards = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public MTGCard getItem(int i) {
        return cards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final CardHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_card, null);
            holder = new CardHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (CardHolder) convertView.getTag();
        }

        MTGCard card = getItem(position);
        holder.name.setText(card.getName());
        holder.rarity.setText(card.getRarity().substring(0,1));
        holder.cost.setText(card.getManaCost());

        return convertView;
    }

    class CardHolder {
        TextView name;
        TextView rarity;
        TextView cost;

        CardHolder(View row){
            name = (TextView) row.findViewById(R.id.card_name);
            rarity = (TextView) row.findViewById(R.id.card_rarity);
            cost = (TextView) row.findViewById(R.id.card_cost);
        }
    }

}
