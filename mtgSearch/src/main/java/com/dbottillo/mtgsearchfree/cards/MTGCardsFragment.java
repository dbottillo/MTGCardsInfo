package com.dbottillo.mtgsearchfree.cards;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.adapters.CardsPagerAdapter;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.dialog.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.helper.DialogHelper;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.util.UIUtil;

import java.util.ArrayList;

public class MTGCardsFragment extends BasicFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String POSITION = "position";
    public static final String TITLE = "set_name";
    public static final String DECK = "deck";

    private ViewPager viewPager;
    private ArrayList<MTGCard> cards;
    private CardsPagerAdapter adapter;

    private int position;
    PagerTabStrip pagerTabStrip;

    MenuItem actionImage;
    FloatingActionButton addToDeck;


    public static MTGCardsFragment newInstance(int position, String title, boolean deck) {
        MTGCardsFragment fragment = new MTGCardsFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(TITLE, title);
        args.putBoolean(DECK, deck);
        fragment.setArguments(args);
        return fragment;
    }

    public MTGCardsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cards, container, false);

        position = getArguments().getInt(POSITION);
        cards = MTGApp.Companion.getCardsToDisplay();

        if (cards != null) {
            viewPager = (ViewPager) rootView.findViewById(R.id.pager);
            boolean deck = getArguments().getBoolean(DECK);

            pagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_tab_strip);
            pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.white));
            pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.color_primary));
            pagerTabStrip.setTextColor(getResources().getColor(R.color.white));

            viewPager.addOnPageChangeListener(this);

            adapter = new CardsPagerAdapter(getActivity().getSupportFragmentManager(), deck);
            adapter.setCards(cards);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position);

            addToDeck = (FloatingActionButton) rootView.findViewById(R.id.card_add_to_deck);
            addToDeck.setOnClickListener(this);
            RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) addToDeck.getLayoutParams();
            if (getIsPortrait()) {
                par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            } else {
                par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                par.rightMargin = UIUtil.dpToPx(getActivity(), 16);
            }
            addToDeck.setLayoutParams(par);

            setActionBarTitle(getArguments().getString(TITLE));
            setHasOptionsMenu(true);
        } else {
            getActivity().finish();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshColor(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset != 0.0) {
            if (positionOffset < 0.5) {
                setFabScale(1.0f - positionOffset);
            } else {
                setFabScale(positionOffset);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        refreshColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setFabScale(1.0f);
        }

    }

    private void setFabScale(float value) {
        addToDeck.setScaleX(value);
        addToDeck.setScaleY(value);
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

        if (getActivity() != null && getSharedPreferences().getBoolean(BasicFragment.Companion.getPREF_SHOW_IMAGE(), true)) {
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
            boolean showImage = getSharedPreferences().getBoolean(BasicFragment.Companion.getPREF_SHOW_IMAGE(), true);
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean(BasicFragment.Companion.getPREF_SHOW_IMAGE(), !showImage);
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

    @Override
    public void onClick(View v) {
        MTGCard card = cards.get(viewPager.getCurrentItem());
        DialogHelper.Companion.open(getDbActivity(), "add_to_deck", AddToDeckFragment.newInstance(card));
    }
}
