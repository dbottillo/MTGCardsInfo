package com.dbottillo.mtgsearchfree.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.ui.CommonCardsActivity;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.MaterialWrapper;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.adapters.CardsPagerAdapter;
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardsActivity extends CommonCardsActivity implements ViewPager.OnPageChangeListener, CardsActivityView {

    protected static final String KEY_SEARCH = "Search";
    protected static final String KEY_SET = "Set";
    protected static final String KEY_CARD = "Card";
    protected static final String KEY_FAV = "Fav";
    protected static final String KEY_DECK = "Deck";
    protected static final String POSITION = "Position";

    public static Intent newInstance(Context context, MTGSet set, int position, MTGCard card) {
        Intent intent = new Intent(context, CardsActivity.class);
        intent.putExtra(CardsActivity.POSITION, position);
        intent.putExtra(CardsActivity.KEY_SET, set);
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


    @BindView(R.id.cards_view_pager)
    ViewPager viewPager;
    @BindView(R.id.cards_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.card_add_to_deck)
    FloatingActionButton fabButton;

    private CardsPagerAdapter adapter;

    @Inject
    CardsActivityPresenter cardsPresenter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cards);

        ButterKnife.bind(this);
        setupView();

        getMtgApp().getUiGraph().inject(this);

        cardsPresenter.init(this, getIntent());
    }

    private void setupView() {
        setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MaterialWrapper.setElevation(getToolbar(), 0f);

        /*pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.white));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.color_primary));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.white));*/
        tabLayout.setupWithViewPager(viewPager);
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) fabButton.getLayoutParams();
        if (isPortrait()) {
            par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else {
            par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            par.rightMargin = UIUtil.dpToPx(this, 16);
        }
        fabButton.setLayoutParams(par);
        viewPager.addOnPageChangeListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

    public String getPageTrack() {
        if (cardsPresenter.isDeck()) {
            return "/deck";
        }
        return "/cards";
    }

    @Override
    public void updateAdapter(CardsCollection cards, boolean showImage, int startPosition) {
        LOG.d();
        adapter = new CardsPagerAdapter(this, showImage, cards);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition);
        syncMenu();
    }

    public void favClicked() {
        LOG.d();
        cardsPresenter.favClicked(getCurrentCard());
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
        cardsPresenter.toggleImage(show);
    }

    @Override
    protected void syncMenu() {
        cardsPresenter.updateMenu(getCurrentCard());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setFabScale(1.0f);
            syncMenu();
        }
    }

    public void onPageSelected(int position) {
        syncMenu();
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

    @OnClick(R.id.card_add_to_deck)
    public void addToDeck(View view) {
        LOG.d();
        openDialog("add_to_deck", AddToDeckFragment.Companion.newInstance(getCurrentCard()));
    }

    @Override
    public void updateTitle(@NotNull String name) {
        setTitle(name);
    }

    @Override
    public void updateTitle(int resource) {
        setTitle(getString(resource));
    }

    @Override
    public void showFavMenuItem() {
        if (favMenuItem != null) {
            favMenuItem.setVisible(true);
        }
    }

    @Override
    public void updateFavMenuItem(int text, int icon) {
        if (favMenuItem != null) {
            favMenuItem.setTitle(getString(text));
            favMenuItem.setIcon(icon);
        }
    }

    @Override
    public void hideFavMenuItem() {
        if (favMenuItem != null) {
            favMenuItem.setVisible(false);
        }
    }

    @Override
    public void setImageMenuItemChecked(boolean checked) {
        if (imageMenuItem != null){
            imageMenuItem.setChecked(checked);
        }
    }
}
