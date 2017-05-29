package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public final class CardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private List<MTGCard> cards;
    private boolean gridMode;
    private boolean isASearch;
    private OnCardListener onCardListener;
    private int menuRes;
    private String title;
    private CardFilter cardFilter;
    private int colorFilterActive = -1;

    public static CardsAdapter list(List<MTGCard> cards, boolean isASearch, int menuRes, String title, CardFilter cardFilter) {
        LOG.d();
        return new CardsAdapter(cards, false, isASearch, menuRes, title, cardFilter);
    }

    public static CardsAdapter grid(List<MTGCard> cards, boolean isASearch, int menuRes, String title, CardFilter cardFilter) {
        LOG.d();
        return new CardsAdapter(cards, true, isASearch, menuRes, title, cardFilter);
    }

    private CardsAdapter(List<MTGCard> cards, boolean gridMode, boolean isASearch, int menuRes, String title, CardFilter cardFilter) {
        this.cards = cards;
        this.gridMode = gridMode;
        this.isASearch = isASearch;
        this.menuRes = menuRes;
        this.title = title;
        this.cardFilter = cardFilter;
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
            if (cardFilter == null){
                holder.subTitle.setVisibility(View.GONE);
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            } else {
                holder.subTitle.setVisibility(View.VISIBLE);
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
                SpannableString spannableString = new SpannableString("WUBRG - ALE - CURM");
                if (colorFilterActive == -1) {
                    colorFilterActive = ContextCompat.getColor(holder.itemView.getContext(), R.color.color_accent);
                }
                checkSpannable(spannableString, cardFilter.white, 0);
                checkSpannable(spannableString, cardFilter.blue, 1);
                checkSpannable(spannableString, cardFilter.black, 2);
                checkSpannable(spannableString, cardFilter.red, 3);
                checkSpannable(spannableString, cardFilter.green, 4);

                checkSpannable(spannableString, cardFilter.artifact, 8);
                checkSpannable(spannableString, cardFilter.land, 9);
                checkSpannable(spannableString, cardFilter.eldrazi, 10);

                checkSpannable(spannableString, cardFilter.common, 14);
                checkSpannable(spannableString, cardFilter.uncommon, 15);
                checkSpannable(spannableString, cardFilter.rare, 16);
                checkSpannable(spannableString, cardFilter.mythic, 17);
                holder.subTitle.setText(spannableString);
            }
            return;
        }

        final CardViewHolder holder = (CardViewHolder) originalHolder;
        final MTGCard card = cards.get(position-1);
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
                    onCardListener.onCardSelected(card, holder.getAdapterPosition()-1, v.findViewById(R.id.grid_item_card_image));
                }
            }
        });
    }

    private void checkSpannable(SpannableString spannableString, boolean on, int start) {
        if (on){
            spannableString.setSpan(new ForegroundColorSpan(colorFilterActive), start, start+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cards.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
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

    public String getTitle() {
        return title;
    }

    public CardFilter getCardFilter() {
        return cardFilter;
    }

    public int getMenuOption() {
        return menuRes;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView subTitle;
        ImageButton type;
        ImageButton settings;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle= (TextView) itemView.findViewById(R.id.sub_title);
            type = (ImageButton) itemView.findViewById(R.id.cards_view_type);
            settings = (ImageButton) itemView.findViewById(R.id.cards_settings);
        }
    }

}
