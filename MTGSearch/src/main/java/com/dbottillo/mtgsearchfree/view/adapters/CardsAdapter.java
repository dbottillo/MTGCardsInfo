package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public final class CardsAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private CardsBucket bucket;
    private boolean gridMode;
    private boolean isASearch;
    private OnCardListener onCardListener;
    private int menuRes;

    public static CardsAdapter list(CardsBucket cards, boolean isASearch, int menuRes) {
        LOG.d();
        return new CardsAdapter(cards, false, isASearch, menuRes);
    }

    public static CardsAdapter grid(CardsBucket cards, boolean isASearch, int menuRes) {
        LOG.d();
        return new CardsAdapter(cards, true, isASearch, menuRes);
    }

    private CardsAdapter(CardsBucket bucket, boolean gridMode, boolean isASearch, int menuRes) {
        this.bucket = bucket;
        this.gridMode = gridMode;
        this.isASearch = isASearch;
        this.menuRes = menuRes;
    }

    public void setOnCardListener(OnCardListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int columns = context.getResources().getInteger(R.integer.cards_grid_column_count);
        View v = LayoutInflater.from(parent.getContext()).inflate(gridMode ? R.layout.grid_item_card : R.layout.row_card, parent, false);
        if (gridMode) {
            int height = (int) ((parent.getMeasuredWidth() / (double) columns) * MTGCardView.RATIO_CARD);
            v.setMinimumHeight(height);
        }
        return new CardViewHolder(v, gridMode);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        final MTGCard card = bucket.getCards().get(position);
        final Context context = holder.parent.getContext();
        if (gridMode) {
            holder.loader.setVisibility(View.VISIBLE);
            holder.image.setContentDescription(card.getName());
            Picasso.with(context.getApplicationContext()).load(card.getImage())
                    .error(R.drawable.left_debug)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.loader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.loader.setVisibility(View.GONE);
                        }
                    });
        } else {
            CardAdapterHelper.bindView(context, card, holder, isASearch);
            CardAdapterHelper.setupMore(holder, context, card, position, menuRes, onCardListener);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardListener != null) {
                    onCardListener.onCardSelected(card, holder.getAdapterPosition(), v.findViewById(R.id.grid_item_card_image));
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
        return bucket.getCards().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public CardsBucket getBucket() {
        return bucket;
    }

    public OnCardListener getOnCardListener() {
        return onCardListener;
    }
}
