package com.dbottillo.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.mtgsearch.R;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardsFragment extends DBFragment implements ViewPager.OnPageChangeListener {

    public static final String CARDS = "cards";
    public static final String POSITION = "position";
    public static final String SET_NAME = "set_name";

    private ViewPager viewPager;
    private ArrayList<MTGCard> cards;
    private CardsPagerAdapter adapter;

    private int position;

    MenuItem actionImage;

    public static MTGCardsFragment newInstance(ArrayList<MTGCard> cards, int position, String setName) {
        MTGCardsFragment fragment = new MTGCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CARDS, cards);
        args.putInt(POSITION, position);
        args.putString(SET_NAME, setName);
        fragment.setArguments(args);
        return fragment;
    }

    public MTGCardsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cards, container, false);

        position = getArguments().getInt(POSITION);
        cards = getArguments().getParcelableArrayList(CARDS);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);

        adapter = new CardsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        viewPager.setOnPageChangeListener(this);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        setActionBarTitle(getArguments().getString(SET_NAME));
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(cards.get(position).getName());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public String getPageTrack() {
        return "/cards_viewpager";
    }

    private class CardsPagerAdapter extends FragmentStatePagerAdapter {
        public CardsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MTGCardFragment.newInstance(cards.get(position));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.card, menu);

        actionImage = menu.findItem(R.id.action_image);

        if (getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true)){
            actionImage.setIcon(R.drawable.image_off);
        }else{
            actionImage.setIcon(R.drawable.image_on);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_image) {
            getApp().trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_CLICK, "image_on_off");
            boolean showImage = getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true);
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean(PREF_SHOW_IMAGE, !showImage);
            editor.commit();
            adapter.notifyDataSetChanged();
            viewPager.invalidate();
            if (!showImage) {
                actionImage.setIcon(R.drawable.image_off);
            } else {
                actionImage.setIcon(R.drawable.image_on);
            }
            return true;
        } else {
        }

        return false;
    }
}
