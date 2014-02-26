package com.dbottillo.mtgsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.helper.DBAsyncTask;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import java.lang.reflect.Array;
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
        public CharSequence getPageTitle(int position) {
            return cards.get(position).getName();
        }
    }
}
