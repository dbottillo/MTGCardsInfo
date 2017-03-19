package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public final class CardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private CardsBucket bucket;
    private boolean gridMode;
    private boolean isASearch;
    private OnCardListener onCardListener;
    private int menuRes;
    private String title;

    public static CardsAdapter list(CardsBucket cards, boolean isASearch, int menuRes, String title) {
        LOG.d();
        return new CardsAdapter(cards, false, isASearch, menuRes, title);
    }

    public static CardsAdapter grid(CardsBucket cards, boolean isASearch, int menuRes, String title) {
        LOG.d();
        return new CardsAdapter(cards, true, isASearch, menuRes, title);
    }

    private CardsAdapter(CardsBucket bucket, boolean gridMode, boolean isASearch, int menuRes, String title) {
        this.bucket = bucket;
        this.gridMode = gridMode;
        this.isASearch = isASearch;
        this.menuRes = menuRes;
        this.title = title;
    }

    public void setOnCardListener(OnCardListener onCardListener) {
        this.onCardListener = onCardListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ITEM_VIEW_TYPE_HEADER){
            return new HeaderViewHolder(inflater.inflate(R.layout.cards_header, parent, false));
        }

        Context context = parent.getContext();
        int columns = context.getResources().getInteger(R.integer.cards_grid_column_count);
        View v = inflater.inflate(gridMode ? R.layout.grid_item_card : R.layout.row_card, parent, false);
        if (gridMode) {
            int height = (int) ((parent.getMeasuredWidth() / (double) columns) * MTGCardView.RATIO_CARD);
            v.setMinimumHeight(height);
        }
        return new CardViewHolder(v, gridMode);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder originalHolder, int position) {
        if (position == ITEM_VIEW_TYPE_HEADER){
            final HeaderViewHolder holder = (HeaderViewHolder) originalHolder;
            holder.title.setText(title);
            holder.type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCardListener.onCardsViewTypeSelected();
                }
            });
            holder.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCardListener.onCardsSettingSelected();
                }
            });
            if (gridMode) {
                holder.type.setImageResource(R.drawable.cards_grid_type);
            } else {
                holder.type.setImageResource(R.drawable.cards_list_type);
            }
            return;
        }

        final CardViewHolder holder = (CardViewHolder) originalHolder;
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
        return bucket.getCards().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
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

    public String getTitle() {
        return title;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageButton type;
        ImageButton settings;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            type = (ImageButton) itemView.findViewById(R.id.cards_view_type);
            settings = (ImageButton) itemView.findViewById(R.id.cards_settings);
        }
    }
}
