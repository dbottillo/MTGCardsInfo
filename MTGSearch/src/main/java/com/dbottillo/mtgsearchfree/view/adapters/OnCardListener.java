package com.dbottillo.mtgsearchfree.view.adapters;

import android.view.MenuItem;
import android.view.View;

import com.dbottillo.mtgsearchfree.model.MTGCard;

public interface OnCardListener {
    void onCardSelected(MTGCard card, int position, View view);
    void onOptionSelected(MenuItem menuItem, MTGCard card, int position);
}