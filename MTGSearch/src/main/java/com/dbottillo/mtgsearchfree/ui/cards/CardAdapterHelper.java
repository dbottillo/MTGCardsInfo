package com.dbottillo.mtgsearchfree.ui.cards;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.MTGCard;

public final class CardAdapterHelper {

    private CardAdapterHelper() {

    }

    static void bindView(Context context, MTGCard card, ListCardViewHolder holder, boolean isASearch) {
        bindView(context, card, holder, isASearch, false);
    }

    public static void bindView(Context context, MTGCard card, ListCardViewHolder holder, boolean isASearch, boolean deck) {
        holder.getName().setText(context.getString(R.string.row_card_name, (deck ? card.getQuantity() + " " : ""), card.getName()));

        int rarityColor = R.color.common;
        if (card.getRarity().equalsIgnoreCase(CardFilter.FILTER_UNCOMMON)) {
            rarityColor = R.color.uncommon;
        } else if (card.getRarity().equalsIgnoreCase(CardFilter.FILTER_RARE)) {
            rarityColor = R.color.rare;
        } else if (card.getRarity().equalsIgnoreCase(CardFilter.FILTER_MYHTIC)) {
            rarityColor = R.color.mythic;
        }
        holder.getRarity().setTextColor(context.getResources().getColor(rarityColor));
        if (card.getRarity().length() > 0) {
            holder.getRarity().setText(card.getRarity());
        } else {
            holder.getRarity().setText("");
        }

        if (card.getManaCost() != null) {
            holder.getCost().setText(card.getManaCost().replace("{", "").replace("}", ""));
            holder.getCost().setTextColor(card.getMtgColor(context));
        } else {
            holder.getCost().setText("-");
        }

        if (isASearch) {
            holder.getSetName().setVisibility(View.VISIBLE);
            holder.getSetName().setText(card.getSet().getName());
        } else {
            holder.getSetName().setVisibility(View.GONE);
        }

        GradientDrawable indicator = (GradientDrawable) holder.getIndicator().getBackground();
        indicator.setColor(card.getMtgColor(context));

    }

    public static void setupMore(final ListCardViewHolder holder,
                                 final Context context,
                                 final MTGCard card,
                                 final int position,
                                 final int menuRes,
                                 final OnCardListener onCardListener) {
        if (menuRes > 0 && onCardListener != null) {
            holder.getMore().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.getMore());
                    final Menu menu = popupMenu.getMenu();

                    popupMenu.getMenuInflater().inflate(menuRes, menu);
                    if (menu.size() > 4) {
                        MenuItem moveOne = menu.getItem(3);
                        moveOne.setTitle(card.isSideboard() ? R.string.move_card_to_deck : R.string.move_card_to_sideboard);
                        MenuItem moveAll = menu.getItem(4);
                        moveAll.setTitle(card.isSideboard() ? R.string.move_all_card_to_deck : R.string.move_all_card_to_sideboard);
                    }
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
            holder.getMore().setVisibility(View.GONE);
        }
    }
}
