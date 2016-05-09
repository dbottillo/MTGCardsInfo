package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private List<MTGCard> cards;
    private boolean grid;
    private boolean isASearch;
    private OnCardListener onCardListener;
    private int menuRes;

    public static CardsAdapter list(List<MTGCard> cards, boolean isASearch, int menuRes) {
        LOG.d();
        return new CardsAdapter(cards, false, isASearch, menuRes);
    }

    public static CardsAdapter grid(List<MTGCard> cards, boolean isASearch, int menuRes) {
        LOG.d();
        return new CardsAdapter(cards, true, isASearch, menuRes);
    }

    private CardsAdapter(List<MTGCard> cards, boolean grid, boolean isASearch, int menuRes) {
        this.cards = cards;
        this.grid = grid;
        this.isASearch = isASearch;
        this.menuRes = menuRes;
    }

    public void setOnCardListener(OnCardListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(grid ? R.layout.grid_item_card : R.layout.row_card, parent, false);
        if (grid) {
            int height = (int) (parent.getMeasuredWidth() / MTGCardView.RATIO_CARD);
            UIUtil.setHeight(v, height);
        }
        return new CardViewHolder(v, grid);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        final MTGCard card = cards.get(position);
        Context context = holder.parent.getContext();
        if (grid) {
            Picasso.with(context).load(card.getImage())
                    .error(R.drawable.leak_canary_icon)
                    .placeholder(R.drawable.card_loader)
                    .into(holder.image);
        } else {
            CardAdapterHelper.bindView(context, card, holder, isASearch);
            CardAdapterHelper.setupMore(holder, context, card, position, menuRes, onCardListener);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardListener != null) {
                    onCardListener.onCardSelected(card, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public List<MTGCard> getCards() {
        return cards;
    }

    public OnCardListener getOnCardListener() {
        return onCardListener;
    }
}
