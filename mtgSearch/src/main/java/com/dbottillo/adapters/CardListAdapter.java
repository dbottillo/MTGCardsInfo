package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.GameCard;
import com.dbottillo.resources.MTGCard;

import java.util.List;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class CardListAdapter extends BaseAdapter {

    private List<GameCard> cards;
    private Context context;
    private LayoutInflater inflater;
    private boolean isASearch;

    public CardListAdapter(Context context, List<GameCard> cards, boolean isASearch) {
        this.cards = cards;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.isASearch = isASearch;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public GameCard getItem(int i) {
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

        GameCard card = getItem(position);
        holder.name.setText(card.getName());

        if (BuildConfig.magic) {
            int rarityColor = R.color.common;
            if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_UNCOMMON)) {
                rarityColor = R.color.uncommon;
            } else if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_RARE)) {
                rarityColor = R.color.rare;
            } else if (card.getRarity().equalsIgnoreCase(FilterHelper.FILTER_MYHTIC)) {
                rarityColor = R.color.mythic;
            }
            holder.rarity.setTextColor(context.getResources().getColor(rarityColor));
        }
        if (card.getRarity().length() > 0) {
            holder.rarity.setText(card.getRarity().substring(0, 1));
        } else {
            holder.rarity.setText("");
        }

        if (card.getManaCost() != null) {
            if (BuildConfig.magic) {
                holder.cost.setText(card.getManaCost().replace("{", "").replace("}", ""));
            } else {
                holder.cost.setText(card.getManaCost());
            }
        } else {
            holder.cost.setText("");
        }

        holder.position.setText(position + "");

        if (isASearch) {
            holder.setName.setVisibility(View.VISIBLE);
            holder.setName.setText(card.getSetName());
        }

        if (BuildConfig.magic) {
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
        }

        return convertView;
    }

    class CardHolder {
        View parent;
        TextView name;
        TextView setName;
        TextView rarity;
        TextView cost;
        TextView position;

        CardHolder(View row) {
            parent = row.findViewById(R.id.card_parent);
            name = (TextView) row.findViewById(R.id.card_name);
            setName = (TextView) row.findViewById(R.id.card_set_name);
            rarity = (TextView) row.findViewById(R.id.card_rarity);
            cost = (TextView) row.findViewById(R.id.card_cost);
            position = (TextView) row.findViewById(R.id.card_position);
        }
    }

}
