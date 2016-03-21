package com.dbottillo.mtgsearchfree.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.cards.CardsHelper;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.resources.Deck;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.CardFilterView;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.adapters.CardsPagerAdapter;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CardsActivity extends CommonCardsActivity implements CardsView, ViewPager.OnPageChangeListener, CardFilterView {

    private static final String KEY_SEARCH = "Search";
    private static final String KEY_SET = "Set";
    private static final String KEY_DECK = "Deck";
    private static final String POSITION = "Position";

    public static Intent newInstance(Context context, MTGSet gameSet, int position) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_SET, gameSet);
        return intent;
    }

    public static Intent newInstance(Context context, Deck deck, int position) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_DECK, deck);
        return intent;
    }

    public static Intent newInstance(Context context, String search, int position) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_SEARCH, search);
        return intent;
    }

    private MTGSet set = null;
    private Deck deck = null;
    private String search = null;
    private int startPosition = 0;
    private CardsBucket bucket;
    private CardFilter currentFilter;

    @Bind(R.id.cards_view_pager)
    ViewPager viewPager;
    @Bind(R.id.cards_tab_strip)
    PagerTabStrip pagerTabStrip;
    @Bind(R.id.card_add_to_deck)
    FloatingActionButton fabButton;

    CardsPagerAdapter adapter;

    @Inject
    CardsPresenter cardsPresenter;
    @Inject
    CardFilterPresenter filterPresenter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cards);

        ButterKnife.bind(this);

        setupView();

        MTGApp.dataGraph.inject(this);
        cardsPresenter.init(this);
        filterPresenter.init(this);

        if (getIntent() != null) {
            if (getIntent().hasExtra(KEY_SET)) {
                set = getIntent().getParcelableExtra(KEY_SET);
                getSupportActionBar().setTitle(set.getName());

            } else if (getIntent().hasExtra(KEY_SEARCH)) {
                search = getIntent().getStringExtra(KEY_SEARCH);

            } else if (getIntent().hasExtra(KEY_DECK)) {
                deck = getIntent().getParcelableExtra(KEY_DECK);
            }
        }
        startPosition = getIntent().getIntExtra(POSITION, 0);

        cardsPresenter.loadIdFavourites();

    }

    public void setupView() {
        setupToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MaterialWrapper.setElevation(toolbar, 0f);

        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.white));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.color_primary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.white));
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) fabButton.getLayoutParams();
        if (isPortrait) {
            par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else {
            par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            par.rightMargin = UIUtil.dpToPx(this, 16);
        }
        fabButton.setLayoutParams(par);
        viewPager.addOnPageChangeListener(this);
    }

    public String getPageTrack() {
        if (deck != null) {
            return "/deck";
        }
        return "/cards";
    }

    private void reloadAdapter() {
        boolean showImage = getSharedPreferences().getBoolean(BasicFragment.PREF_SHOW_IMAGE, true);
        adapter = new CardsPagerAdapter(this, deck != null, showImage, bucket.getCards());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition);
        updateMenu();
    }

    public void favClicked() {
        MTGCard currentCard = adapter.getItem(viewPager.getCurrentItem());
        if (Arrays.asList(idFavourites).contains(currentCard.getMultiVerseId())) {
            cardsPresenter.removeFromFavourite(currentCard);
        } else {
            cardsPresenter.saveAsFavourite(currentCard);
        }
    }

    public MTGCard getCurrentCard() {
        return adapter.getItem(viewPager.getCurrentItem());
    }

    public void toggleImage(boolean show) {
        reloadAdapter();
    }

    public void favIdLoaded(int[] favourites) {
        idFavourites = favourites;

        if (adapter == null) {
            // first time needs to load cards
            if (set != null) {
                cardsPresenter.loadCards(set);
            } else if (search != null) {
                // load search
            } else {
                // something very bad happened here
                throw new UnsupportedOperationException();
            }
        } else {
            updateMenu();
        }
    }

    public void luckyCardsLoaded(ArrayList<MTGCard> cards) {
        throw new UnsupportedOperationException();
    }

    public void cardLoaded(CardsBucket bucket) {
        this.bucket = bucket;
        if (set != null) {
            // needs to load filters first
            filterPresenter.loadFilter();
        } else {
            reloadAdapter();
        }
    }

    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setFabScale(1.0f);
            updateMenu();
        }
    }

    public void onPageSelected(int position) {
        updateMenu();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset != 0.0) {
            if (positionOffset < 0.5) {
                setFabScale(1.0f - positionOffset);
            } else {
                setFabScale(positionOffset);
            }
        }
    }

    public void setFabScale(float value) {
        fabButton.setScaleX(value);
        fabButton.setScaleY(value);
    }

    @Override
    public void filterLoaded(CardFilter filter) {
        ArrayList<MTGCard> allCards = bucket.getCards();
        ArrayList<MTGCard> filteredCards = new ArrayList<>();
        CardsHelper.filterCards(filter, allCards, filteredCards);
        bucket.setCards(filteredCards);
        reloadAdapter();
    }
}
