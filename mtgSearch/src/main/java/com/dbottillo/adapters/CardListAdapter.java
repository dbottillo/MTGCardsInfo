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

    public interface OnCardListener {
        void onAddToDeck(MTGCard card);
    }

    private List<MTGCard> cards;
    private Context context;
    private LayoutInflater inflater;
    private boolean isASearch;
    private OnCardListener onCardListener;

    public CardListAdapter(Context context, List<MTGCard> cards, boolean isASearch) {
        this.cards = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.isASearch = isASearch;
    }

    public void setOnCardListener(OnCardListener onCardListener) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        final CardViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_card, null);
            holder = new CardViewHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (CardViewHolder) convertView.getTag();
        }

        final MTGCard card = getItem(position);
        CardAdapterHelper.bindView(context, card, holder, isASearch);

        holder.addToDeck.setTag(card);
        holder.addToDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardListener != null) {
                    onCardListener.onAddToDeck((MTGCard) v.getTag());
                }
            }
        });

        return convertView;
    }

}
