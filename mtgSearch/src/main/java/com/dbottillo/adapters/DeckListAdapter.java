package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.resources.Deck;

import java.util.List;

public class DeckListAdapter extends BaseAdapter {

    private List<Deck> decks;
    private Context context;
    private LayoutInflater inflater;

    public DeckListAdapter(Context context, List<Deck> cards) {
        this.decks = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return decks.size();
    }

    @Override
    public Deck getItem(int i) {
        return decks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final DeckHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_deck, null);
            holder = new DeckHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (DeckHolder) convertView.getTag();
        }

        Deck deck = getItem(position);
        holder.name.setText(deck.getName());

        return convertView;
    }

    class DeckHolder {
        TextView name;

        DeckHolder(View row) {
            name = (TextView) row.findViewById(R.id.deck_name);
        }
    }

}
