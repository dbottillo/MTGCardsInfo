package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.resources.Deck;

import java.util.List;

public class DeckListAdapter extends BaseAdapter {

    public interface OnDeckListener{
        void onDeckSelected(Deck deck);
        void onDeckDelete(Deck deck);
    }

    private List<Deck> decks;
    private Context context;
    private LayoutInflater inflater;
    private OnDeckListener listener;

    public DeckListAdapter(Context context, List<Deck> cards, OnDeckListener listener) {
        this.decks = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
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
            convertView = inflater.inflate(R.layout.row_deck, parent, false);
            holder = new DeckHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (DeckHolder) convertView.getTag();
        }

        final Deck deck = getItem(position);
        holder.name.setText(deck.getName());
        holder.number.setText(context.getString(R.string.deck_subtitle, deck.getNumberOfCards() + ""));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeckDelete(deck);
            }
        });
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeckSelected(deck);
            }
        });

        return convertView;
    }

    class DeckHolder {
        View parent;
        TextView name;
        TextView number;
        ImageButton delete;

        DeckHolder(View row) {
            parent = row.findViewById(R.id.deck_parent);
            name = (TextView) row.findViewById(R.id.deck_name);
            number = (TextView) row.findViewById(R.id.deck_number);
            delete = (ImageButton) row.findViewById(R.id.delete_deck);
        }
    }

}
