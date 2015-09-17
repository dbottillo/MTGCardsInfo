package com.dbottillo.adapters;

import android.view.MenuItem;

import com.dbottillo.resources.MTGCard;

public interface OnCardListener {
    void onCardSelected(MTGCard card, int position);
    void onOptionSelected(MenuItem menuItem, MTGCard card, int position);
}