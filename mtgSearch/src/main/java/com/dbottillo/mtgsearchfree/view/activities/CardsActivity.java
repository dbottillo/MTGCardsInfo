package com.dbottillo.mtgsearchfree.view.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.CardFilterView;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.DecksView;
import com.dbottillo.mtgsearchfree.view.adapters.CardsPagerAdapter;
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardsActivity extends CommonCardsActivity implements CardsView, ViewPager.OnPageChangeListener, CardFilterView, DecksView {

    private static final String KEY_SEARCH = "Search";
    private static final String KEY_SET = "Set";
    private static final String KEY_CARD = "Card";
    private static final String KEY_FAV = "Fav";
    private static final String KEY_DECK = "Deck";
    private static final String POSITION = "Position";

    public static Intent newInstance(Context context, MTGSet gameSet, int position, MTGCard card) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_SET, gameSet);
        intent.putExtra(CardsActivity.KEY_CARD, card);
        return intent;
    }

    public static Intent newInstance(Context context, Deck deck, int position) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_DECK, deck);
        return intent;
    }

    public static Intent newInstance(Context context, SearchParams search, int position, MTGCard card) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_SEARCH, search);
        intent.putExtra(CardsActivity.KEY_CARD, card);
        return intent;
    }

    public static Intent newFavInstance(Context context, int position) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_FAV, true);
        return intent;
    }

    private MTGSet set = null;
    private Deck deck = null;
    private SearchParams search = null;
    private int startPosition = 0;
    private CardsBucket bucket;
    private boolean favs = false;

    @Bind(R.id.cards_view_pager)
    ViewPager viewPager;
    @Bind(R.id.cards_tab_strip)
    PagerTabStrip pagerTabStrip;
    @Bind(R.id.card_add_to_deck)
    FloatingActionButton fabButton;
    @Bind(R.id.shared_image)
    ImageView sharedImage;

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

        MTGApp.uiGraph.inject(this);
        cardsPresenter.init(this);
        filterPresenter.init(this);

        MTGCard transitionCard = null;
        if (getIntent() != null) {
            if (getIntent().hasExtra(KEY_SET)) {
                set = getIntent().getParcelableExtra(KEY_SET);
                setTitle(set.getName());

            } else if (getIntent().hasExtra(KEY_SEARCH)) {
                search = getIntent().getParcelableExtra(KEY_SEARCH);
                setTitle(getString(R.string.action_search));

            } else if (getIntent().hasExtra(KEY_DECK)) {
                deck = getIntent().getParcelableExtra(KEY_DECK);
                setTitle(deck.getName());

            } else if (getIntent().hasExtra(KEY_FAV)) {
                favs = true;
                setTitle(getString(R.string.action_saved));
            }

            transitionCard = getIntent().getParcelableExtra(KEY_CARD);
            if (transitionCard != null && MTGApp.isActivityTransitionAvailable()) {
                Picasso.with(getApplicationContext()).load(transitionCard.getImage()).into(sharedImage);

                pagerTabStrip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int paddingCard = getResources().getDimensionPixelSize(R.dimen.padding_card_image_half);
                        UIUtil.setMarginTop(sharedImage, pagerTabStrip.getHeight() + paddingCard);
                        pagerTabStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }

            startPosition = getIntent().getIntExtra(POSITION, 0);
        }

        if (transitionCard != null && MTGApp.isActivityTransitionAvailable()) {
            setupEnterAnimation();
        } else {
            cardsPresenter.loadIdFavourites();
        }
    }

    private void setupView() {
        setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MaterialWrapper.setElevation(toolbar, 0f);

        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.white));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.color_primary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.white));
        RelativeLayout.LayoutParams parSharedImage = (RelativeLayout.LayoutParams) sharedImage.getLayoutParams();
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) fabButton.getLayoutParams();
        if (isPortrait) {
            par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            parSharedImage.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else {
            par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            par.rightMargin = UIUtil.dpToPx(this, 16);
        }
        fabButton.setLayoutParams(par);
        viewPager.addOnPageChangeListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int paddingCard = getResources().getDimensionPixelSize(R.dimen.padding_card_image);
        int widthAvailable = size.x - paddingCard * 2;
        if (!isPortrait) {
            widthAvailable = size.x / 2 - paddingCard * 2;
        }
        UIUtil.calculateSizeCardImage(sharedImage, widthAvailable, getResources().getBoolean(R.bool.isTablet));
    }

    public String getPageTrack() {
        if (deck != null) {
            return "/deck";
        }
        return "/cards";
    }

    private void reloadAdapter() {
        LOG.d();
        boolean showImage = getSharedPreferences().getBoolean(BasicFragment.PREF_SHOW_IMAGE, true);
        adapter = new CardsPagerAdapter(this, deck != null, showImage, bucket.getCards());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition);
        updateMenu();
    }

    public void favClicked() {
        LOG.d();
        MTGCard currentCard = adapter.getItem(viewPager.getCurrentItem());
        if (isCardFavourite(currentCard.getMultiVerseId())) {
            cardsPresenter.removeFromFavourite(currentCard, true);
        } else {
            cardsPresenter.saveAsFavourite(currentCard, true);
        }
    }

    public MTGCard getCurrentCard() {
        LOG.d();
        if (adapter == null) {
            return null;
        }
        return adapter.getItem(viewPager.getCurrentItem());
    }

    public void toggleImage(boolean show) {
        LOG.d();
        reloadAdapter();
    }

    public void favIdLoaded(int[] favourites) {
        LOG.d();
        idFavourites = favourites;

        if (adapter == null) {
            // first time needs to loadSet cards
            if (set != null) {
                cardsPresenter.loadCards(set);
            } else if (search != null) {
                // loadSet search
                cardsPresenter.doSearch(search);

            } else if (deck != null) {
                cardsPresenter.loadDeck(deck);

            } else if (favs) {
                cardsPresenter.loadFavourites();

            } else {// something very bad happened here
                throw new UnsupportedOperationException();
            }
        } else {
            updateMenu();
        }

    }

    @Override
    public void cardTypePreferenceChanged(boolean grid) {

    }

    public void cardLoaded(CardsBucket bucket) {
        LOG.d();
        this.bucket = bucket;
        if (set != null || favs) {
            // needs to loadSet filters first
            filterPresenter.loadFilter();
        } else {
            CardsHelper.sortCards(getSharedPreferences(), bucket);
            reloadAdapter();
        }
    }

    @Override
    public void deckLoaded(DeckBucket bucket) {
        LOG.d();
        this.bucket = bucket;
        reloadAdapter();
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
        LOG.d();
        this.bucket = CardsHelper.filterCards(filter, bucket);
        CardsHelper.sortCards(getSharedPreferences(), bucket);
        reloadAdapter();
    }

    @OnClick(R.id.card_add_to_deck)
    public void addToDeck(View view) {
        LOG.d();
        MTGCard card = bucket.getCards().get(viewPager.getCurrentItem());
        openDialog("add_to_deck", AddToDeckFragment.newInstance(card));
    }

    @Override
    public void decksLoaded(List<Deck> decks) {
        throw new UnsupportedOperationException();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.change_bound_with_arc);
        transition.setDuration(200);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                cardsPresenter.loadIdFavourites();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }
}
