package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dbottillo.R;
import com.dbottillo.resources.MTGCard;

import java.util.List;

public class CardListAdapter extends BaseAdapter {

    private List<MTGCard> cards;
    private Context context;
    private LayoutInflater inflater;
    private boolean isASearch;
    private OnCardListener onCardListener;
    private int menuRes;

    public CardListAdapter(Context context, List<MTGCard> cards, boolean isASearch, int menuRes, OnCardListener onCardListener) {
        this.cards = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.isASearch = isASearch;
        this.menuRes = menuRes;
        this.onCardListener = onCardListener;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        final CardViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_card, parent, false);
            holder = new CardViewHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (CardViewHolder) convertView.getTag();
        }

        final MTGCard card = getItem(position);
        CardAdapterHelper.bindView(context, card, holder, isASearch);
        CardAdapterHelper.setupMore(holder, context, card, position, menuRes, onCardListener);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardListener.onCardSelected(card, position);
            }
        });
        return convertView;
    }

}
