package com.dbottillo.mtgsearchfree.view.adapters;

import android.view.MenuItem;

import com.dbottillo.mtgsearchfree.model.MTGCard;

public interface OnCardListener {
    void onCardSelected(MTGCard card, int position);
    void onOptionSelected(MenuItem menuItem, MTGCard card, int position);
}