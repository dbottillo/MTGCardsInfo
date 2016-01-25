package com.dbottillo.mtgsearchfree.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dbottillo.mtgsearchfree.cards.MTGCardFragment;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.util.ArrayList;

public class CardsPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<MTGCard> cards;
    private boolean fullScreen = false;
    private boolean deck = false;

    public CardsPagerAdapter(FragmentManager fm,  boolean deck) {
        super(fm);
        this.deck = deck;
    }

    public void setCards(ArrayList<MTGCard> cards) {
        this.cards = cards;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    @Override
    public Fragment getItem(int position) {
        return MTGCardFragment.newInstance(cards.get(position), position, fullScreen);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        MTGCard card = cards.get(position);
        if (deck){
            return card.getName() + " ("+card.getQuantity()+")";
        }
        return card.getName();
    }
}
