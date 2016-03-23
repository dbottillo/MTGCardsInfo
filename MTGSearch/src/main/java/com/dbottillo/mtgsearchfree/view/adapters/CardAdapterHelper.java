package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.helper.FilterHelper;
import com.dbottillo.mtgsearchfree.model.MTGCard;

public final class CardAdapterHelper {

    private CardAdapterHelper() {

    }

    protected static void bindView(Context context, MTGCard card, CardViewHolder holder, boolean isASearch) {
        bindView(context, card, holder, isASearch, false);
    }

    protected static void bindView(Context context, MTGCard card, CardViewHolder holder, boolean isASearch, boolean deck) {
        holder.name.setText(context.getString(R.string.row_card_name, (deck ? card.getQuantity() + " " : ""), card.getName()));

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

    }

    public static void setupMore(final CardViewHolder holder, final Context context, final MTGCard card, final int position, final int menuRes, final OnCardListener onCardListener) {
        if (menuRes > 0 && onCardListener != null) {
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.more);
                    final Menu menu = popupMenu.getMenu();

                    popupMenu.getMenuInflater().inflate(menuRes, menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (onCardListener != null) {
                                onCardListener.onOptionSelected(item, card, position);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        } else {
            holder.more.setVisibility(View.GONE);
        }
    }
}
