package com.dbottillo.mtgsearchfree.view.activities;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenter;
import com.dbottillo.mtgsearchfree.util.AnimationUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.SetsView;
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener;
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.view.fragments.SortDialogFragment;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper;
import com.dbottillo.mtgsearchfree.view.views.MTGCardsView;
import com.dbottillo.mtgsearchfree.view.views.MTGSearchView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BasicActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SetsView, CardsView, OnCardListener, SortDialogFragment.SortDialogListener {

    private static final String SEARCH_OPEN = "searchOpen";
    private static final String BG_COLOR_SCROLLVIEW = "bgColorScrollview";
    private static final String TOOLBAR_ELEVATION = "toolbarElevation";
    private static final String SEARCH_PARAMS = "searchParams";

    @BindView(R.id.action_search)
    ImageButton newSearch;

    @BindView(R.id.search_scroll_view)
    ScrollView scrollView;

    @BindView(R.id.cards_list_view)
    MTGCardsView mtgCardsView;

    @BindView(R.id.search_view)
    MTGSearchView searchView;

    @BindView(R.id.second_toolbar)
    Toolbar secondToolbar;

    AnimationDrawable newSearchAnimation;
    ArgbEvaluator argbEvaluator;
    MenuItem cardsShowType;

    int sizeBig = 0;
    boolean searchOpen = false;
    private CardsBucket currentBucket;

    @Inject
    SetsPresenter setsPresenter;

    @Inject
    CardsPresenter cardsPresenter;

    @Inject
    CardsHelper cardsHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(R.string.action_search);
        secondToolbar.setNavigationIcon(R.drawable.ic_close);
        secondToolbar.setTitle(R.string.search_result);
        secondToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        secondToolbar.inflateMenu(R.menu.search_results);
        cardsShowType = secondToolbar.getMenu().findItem(R.id.action_view_type);
        secondToolbar.setOnMenuItemClickListener(this);
        mtgCardsView.setEmptyString(R.string.empty_search);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            searchOpen = savedInstanceState.getBoolean(SEARCH_OPEN);
            scrollView.setBackgroundColor(savedInstanceState.getInt(BG_COLOR_SCROLLVIEW));
            MaterialWrapper.setElevation(toolbar, savedInstanceState.getFloat(TOOLBAR_ELEVATION));
            MaterialWrapper.setElevation(secondToolbar, savedInstanceState.getFloat(TOOLBAR_ELEVATION));
            MaterialWrapper.setStatusBarColor(this, searchOpen ? getResources().getColor(R.color.color_accent_dark) : getResources().getColor(R.color.status_bar));
        } else {
            MaterialWrapper.setElevation(toolbar, 0);
        }

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sizeBig = scrollView.getHeight();
                UIUtil.setMarginTop(mtgCardsView, sizeBig);
                if (searchOpen) {
                    UIUtil.setHeight(scrollView, 0);
                    UIUtil.setMarginTop(mtgCardsView, 0);
                    mtgCardsView.setVisibility(View.VISIBLE);
                }
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sizeToolbar = toolbar.getHeight();
                secondToolbar.setVisibility(View.VISIBLE);
                if (searchOpen) {
                    secondToolbar.setY(0);
                } else {
                    secondToolbar.setY(-sizeToolbar);
                }
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        setupScrollviewListener();

        argbEvaluator = new ArgbEvaluator();

        newSearch.setOnClickListener(this);
        newSearch.setBackgroundResource(R.drawable.anim_search_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newSearch.setElevation(6.0f); // TODO: pre-lollipop version
        }

        getMTGApp().getUiGraph().inject(this);
        setsPresenter.init(this);
        cardsPresenter.init(this);
        setsPresenter.loadSets();

        cardsPresenter.loadCardTypePreference();

        if (savedInstanceState != null){
            SearchParams searchParams = savedInstanceState.getParcelable(SEARCH_PARAMS);
            if (searchParams !=null){
                doSearch(searchParams);
            }
        }
    }

    private void setupScrollviewListener(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupScrollViewListenerM();
        } else {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    computeScrollChanged(scrollView.getScrollY());
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupScrollViewListenerM(){
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                computeScrollChanged(scrollY);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SEARCH_OPEN, searchOpen);
        int color = (Integer) argbEvaluator.evaluate(scrollView.getScrollY() < 400 ? ((float) scrollView.getScrollY() / (float) 400) : 1, getResources().getColor(R.color.color_primary), getResources().getColor(R.color.color_primary_slightly_dark));
        outState.putInt(BG_COLOR_SCROLLVIEW, color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outState.putFloat(TOOLBAR_ELEVATION, toolbar.getElevation());
        }
        outState.putParcelable(SEARCH_PARAMS, searchView.getSearchParams());
    }

    @Override
    public String getPageTrack() {
        return "/search_main";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSearch(SearchParams searchParams) {
        LOG.d();
        TrackingManager.trackSearch(searchParams);
        cardsPresenter.doSearch(searchParams);
        hideIme();
    }

    @Override
    public void onClick(View v) {
        LOG.d();
        SearchParams searchParams = null;
        if (!searchOpen) {
            searchParams = searchView.getSearchParams();
            if (!searchParams.isValid()) {
                Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        final AnimationUtil.LinearInterpolator backgroundInterpolator = AnimationUtil.createLinearInterpolator();
        final int startColor, endColor;
        if (!searchOpen) {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon);
            backgroundInterpolator.fromValue(sizeBig).toValue(0);
            startColor = getResources().getColor(R.color.status_bar);
            endColor = getResources().getColor(R.color.color_accent_dark);
        } else {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon_reverse);
            backgroundInterpolator.fromValue(0).toValue(sizeBig);
            startColor = getResources().getColor(R.color.color_accent_dark);
            endColor = getResources().getColor(R.color.status_bar);
        }
        newSearchAnimation = (AnimationDrawable) newSearch.getBackground();
        newSearchAnimation.start();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int val = (int) backgroundInterpolator.getInterpolation(interpolatedTime);
                UIUtil.setHeight(scrollView, val);
                UIUtil.setMarginTop(mtgCardsView, val);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int color = (Integer) argbEvaluator.evaluate(interpolatedTime, startColor, endColor);
                    SearchActivity.this.getWindow().setStatusBarColor(color);
                }
            }
        };
        final SearchParams finalSearchParams = searchParams;
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!searchOpen) {
                    mtgCardsView.setVisibility(View.VISIBLE);
                    MaterialWrapper.copyElevation(secondToolbar, toolbar);
                    secondToolbar.animate().setDuration(100).translationY(0).start();
                } else {
                    secondToolbar.animate().setDuration(100).translationY(-sizeToolbar).start();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (searchOpen) {
                    mtgCardsView.setVisibility(View.GONE);
                } else {
                    doSearch(finalSearchParams);
                }
                searchOpen = !searchOpen;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(200);
        scrollView.startAnimation(anim);
    }

    private void computeScrollChanged(int amount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(amount < 200 ? 9 * ((float) amount / (float) 200) : 9);
        }
        int color = (Integer) argbEvaluator.evaluate(amount < 400 ? ((float) amount / (float) 400) : 1, getResources().getColor(R.color.color_primary), getResources().getColor(R.color.color_primary_slightly_dark));
        scrollView.setBackgroundColor(color);
    }

    @Override
    public void onBackPressed() {
        if (searchOpen) {
            newSearch.callOnClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.show(getSupportFragmentManager(), sortDialogFragment.getTag());
            sortDialogFragment.setListener(this);
            return true;
        }
        if (item.getItemId() == R.id.action_view_type) {
            cardsPresenter.toggleCardTypeViewPreference();
            return true;
        }
        return false;
    }

    @Override
    public void onSortSelected() {
        LOG.d();
        refreshList();
    }

    @Override
    public void setsLoaded(List<MTGSet> sets) {
        LOG.d();
        searchView.refreshSets(sets);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cardLoaded(CardsBucket bucket) {
        LOG.d();
        currentBucket = bucket;
        refreshList();
    }

    @Override
    public void deckLoaded(DeckBucket bucket) {
        throw new UnsupportedOperationException();
    }

    private void refreshList() {
        LOG.d();
        cardsHelper.sortCards(currentBucket);
        mtgCardsView.loadCards(currentBucket, this);
    }

    @Override
    public void favIdLoaded(int[] favourites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cardTypePreferenceChanged(boolean grid) {
        LOG.d();
        if (grid) {
            mtgCardsView.setGridOn();
            if (cardsShowType != null) {
                cardsShowType.setIcon(R.drawable.cards_list_type);
            }
        } else {
            mtgCardsView.setListOn();
            if (cardsShowType != null) {
                cardsShowType.setIcon(R.drawable.cards_grid_type);
            }
        }
    }

    @Override
    public void onCardSelected(MTGCard card, int position, View view) {
        LOG.d();
        Intent intent;
        if (view != null && MTGApp.isActivityTransitionAvailable()) {
            intent = CardsActivity.newInstance(this, searchView.getSearchParams(), position, card);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setTransitionName(getString(R.string.transition_card));
            }
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.transition_card));
            startActivity(intent, activityOptionsCompat.toBundle());
        } else {
            intent = CardsActivity.newInstance(this, searchView.getSearchParams(), position, null);
            startActivity(intent);
        }
    }

    @Override
    public void onOptionSelected(MenuItem menuItem, MTGCard card, int position) {
        if (menuItem.getItemId() == R.id.action_add_to_deck) {
            DialogHelper.open(this, "add_to_deck", AddToDeckFragment.newInstance(card));

        } else if (menuItem.getItemId() == R.id.action_add_to_favourites) {
            cardsPresenter.saveAsFavourite(card, true);
        }
    }
}
