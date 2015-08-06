package com.dbottillo.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.MTGCard;

import java.util.List;

public class CardListAdapter extends BaseAdapter {

    public interface OnCardListener{
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
        final CardHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_card, null);
            holder = new CardHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (CardHolder) convertView.getTag();
        }

        final MTGCard card = getItem(position);
        holder.name.setText(card.getName());

        int rarityColor = R.color.common;
        if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)) {
            rarityColor = R.color.uncommon;
        } else if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)) {
            rarityColor = R.color.rare;
        } else if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)) {
            rarityColor = R.color.mythic;
        }
        holder.rarity.setTextColor(context.getResources().getColor(rarityColor));
        if (card.getRarity().length() > 0) {
            holder.rarity.setText(card.getRarity());
        } else {
            holder.rarity.setText("");
        }

        if (card.getManaCost() != null) {
            holder.cost.setText(card.getManaCost().replace("{", "").replace("}", ""));
            holder.cost.setTextColor(card.getMtgColor(context));
        } else {
            holder.cost.setText("-");
        }

        if (isASearch) {
            holder.setName.setVisibility(View.VISIBLE);
            holder.setName.setText(card.getSetName());
        } else {
            holder.setName.setVisibility(View.GONE);
        }

        GradientDrawable indicator = (GradientDrawable) holder.indicator.getBackground();
        indicator.setColor(card.getMtgColor(context));


        /*if (BuildConfig.magic) {
            MTGCard mtgCard = (MTGCard) card;
            if (position % 2 == 0) {
                if (mtgCard.isMultiColor() || mtgCard.isAnArtifact() || mtgCard.isALand()) {
                    holder.parent.setBackgroundResource(R.drawable.bg_row_dark);
                } else {
                    if (mtgCard.getColors().contains(MTGCard.WHITE)) {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_white);
                    } else if (mtgCard.getColors().contains(MTGCard.BLUE)) {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_blue);
                    } else if (mtgCard.getColors().contains(MTGCard.BLACK)) {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_black);
                    } else if (mtgCard.getColors().contains(MTGCard.RED)) {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_red);
                    } else if (mtgCard.getColors().contains(MTGCard.GREEN)) {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_green);
                    } else {
                        holder.parent.setBackgroundResource(R.drawable.bg_row_dark);
                    }
                }
            } else {
                holder.parent.setBackgroundResource(R.drawable.bg_row_base);
            }
        } else {
            if (position % 2 == 0) {
                holder.parent.setBackgroundResource(R.drawable.bg_row_dark);
            } else {
                holder.parent.setBackgroundResource(R.drawable.bg_row_base);
            }
        }*/

        holder.addToDeck.setTag(card);
        holder.addToDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardListener != null){
                    onCardListener.onAddToDeck((MTGCard) v.getTag());
                }
            }
        });

        return convertView;
    }

    class CardHolder {
        View parent;
        TextView name;
        TextView setName;
        TextView rarity;
        TextView cost;
        View indicator;
        ImageButton addToDeck;

        CardHolder(View row) {
            parent = row.findViewById(R.id.card_parent);
            name = (TextView) row.findViewById(R.id.card_name);
            setName = (TextView) row.findViewById(R.id.card_set_name);
            rarity = (TextView) row.findViewById(R.id.card_rarity);
            cost = (TextView) row.findViewById(R.id.card_cost);
            indicator = row.findViewById(R.id.card_indicator);
            addToDeck = (ImageButton) row.findViewById(R.id.card_add_deck);
        }
    }

}
