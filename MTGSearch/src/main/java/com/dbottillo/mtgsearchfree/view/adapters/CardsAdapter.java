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
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private CardsBucket bucket;
    private boolean grid;
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

    private CardsAdapter(CardsBucket bucket, boolean grid, boolean isASearch, int menuRes) {
        this.bucket = bucket;
        this.grid = grid;
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
        LOG.e(parent.toString());
        LOG.e(parent.getMeasuredWidth()+"");
        View v = LayoutInflater.from(parent.getContext()).inflate(grid ? R.layout.grid_item_card : R.layout.row_card, parent, false);
        if (grid) {
            int height = (int) ((parent.getMeasuredWidth() / columns) * MTGCardView.RATIO_CARD);
            LOG.e("height: "+height);
            v.setMinimumHeight(height);
            //UIUtil.setHeight(v, height);
        }
        return new CardViewHolder(v, grid);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        final MTGCard card = bucket.getCards().get(position);
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
        int color = -1;
        if (position % 4 == 0){
            color = R.color.color_primary;
        } else if (position % 4 == 1){
            color = R.color.color_primary_dark;
        }  else if (position % 4 == 2){
            color = R.color.color_accent;
        } else {
            color = R.color.mtg_green;
        }
        holder.parent.setBackgroundColor(context.getColor(color));
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
