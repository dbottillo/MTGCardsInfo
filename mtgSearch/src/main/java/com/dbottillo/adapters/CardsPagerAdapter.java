package com.dbottillo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dbottillo.cards.MTGCardFragment;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

public class CardsPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<MTGCard> cards;
    private boolean fullScreen = false;

    public CardsPagerAdapter(FragmentManager fm) {
        super(fm);
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
        return cards.get(position).getName();
    }
}
