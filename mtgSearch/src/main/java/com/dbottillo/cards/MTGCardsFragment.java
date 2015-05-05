package com.dbottillo.cards;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.R;
import com.dbottillo.adapters.CardsPagerAdapter;
import com.dbottillo.base.DBFragment;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.util.UIUtil;

import java.util.ArrayList;

public class MTGCardsFragment extends DBFragment implements ViewPager.OnPageChangeListener {

    public static final String CARDS = "cards";
    public static final String POSITION = "position";
    public static final String SET_NAME = "set_name";

    private ViewPager viewPager;
    private ArrayList<MTGCard> cards;
    private CardsPagerAdapter adapter;

    private int position;
    PagerTabStrip pagerTabStrip;

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
        adapter.setCards(cards);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        viewPager.setOnPageChangeListener(this);

        setActionBarTitle(getArguments().getString(SET_NAME));
        setHasOptionsMenu(true);

        pagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.white));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.color_primary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.white));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshColor(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        refreshColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void refreshColor(int pos) {
        //MTGCard card = (MTGCard) cards.get(pos);
        //pagerTabStrip.setBackgroundColor(card.getMtgColor(getActivity()));
        //MaterialWrapper.setStatusBarColor(getActivity(), card.getMtgColor(getActivity()));
        //MaterialWrapper.setNavigationBarColor(getActivity(), card.getMtgColor(getActivity()));
        //((CardsActivity) getActivity()).setToolbarColor(card.getMtgColor(getActivity()));
    }

    @Override
    public String getPageTrack() {
        return "/cards_viewpager";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cards, menu);

        actionImage = menu.findItem(R.id.action_image);

        MenuItem fullScreenImage = menu.findItem(R.id.action_fullscreen_image);

        fullScreenImage.setVisible(false);

        if (getActivity() != null && getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true)) {
            actionImage.setChecked(true);
            fullScreenImage.setVisible(!getResources().getBoolean(R.bool.isTablet));
        } else {
            actionImage.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_image) {
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, "image_on_off", "");
            boolean showImage = getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true);
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean(PREF_SHOW_IMAGE, !showImage);
            editor.apply();
            adapter.notifyDataSetChanged();
            viewPager.invalidate();
            if (!showImage) {
                actionImage.setChecked(false);
            } else {
                actionImage.setChecked(true);
            }
            return true;
        } else if (i == R.id.action_fullscreen_image) {
            openFullscreen();
            TrackingHelper.getInstance(getActivity()).trackEvent(TrackingHelper.UA_CATEGORY_CARD, "fullscreen", "actionbar");
        }

        return false;
    }

    private void openFullscreen() {
        /*if (getActivity() != null) {
            ((CardsActivity) getActivity()).openFullScreen(viewPager.getCurrentItem());
        }*/
        UIUtil.setHeight(pagerTabStrip, 0);
    }

    public void goTo(int position) {
        viewPager.setCurrentItem(position, false);
    }

    public ArrayList<MTGCard> getCards() {
        return cards;
    }
}
