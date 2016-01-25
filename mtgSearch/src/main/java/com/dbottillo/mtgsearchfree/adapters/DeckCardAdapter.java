package com.dbottillo.mtgsearchfree.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;
import java.util.List;

public class DeckCardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private final Context mContext;
    private List<MTGCard> cards;
    private OnCardListener onCardListener;
    private int menuRes;

    public void add(MTGCard card, int position) {
        position = position == -1 ? getItemCount() : position;
        cards.add(position, card);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (position < getItemCount()) {
            cards.remove(position);
            notifyItemRemoved(position);
        }
    }

    public DeckCardAdapter(Context context, ArrayList<MTGCard> cards, int menuRes, OnCardListener onCardListener) {
        mContext = context;
        this.cards = cards;
        this.menuRes = menuRes;
        this.onCardListener = onCardListener;
    }

    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.row_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        final MTGCard card = cards.get(position);
        CardAdapterHelper.bindView(mContext, card, holder, false, true);
        CardAdapterHelper.setupMore(holder, mContext, card, position, menuRes, onCardListener);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardListener.onCardSelected(card, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}