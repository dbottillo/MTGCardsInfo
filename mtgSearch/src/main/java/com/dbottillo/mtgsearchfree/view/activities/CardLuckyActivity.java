package com.dbottillo.mtgsearchfree.view.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.util.ArrayUtils;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardLuckyActivity extends CommonCardsActivity implements CardsView {

    public static final String CARD = "CARD";
    public static final int LUCKY_BATCH_CARDS = 10;

    private ArrayList<MTGCard> luckyCards = null;

    @Inject
    CardsPresenter cardsPresenter;

    @Inject
    CardsPreferences cardsPreferences;

    @BindView(R.id.card_view)
    MTGCardView cardView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lucky_card);

        setTitle(R.string.lucky_title);

        ButterKnife.bind(this);

        setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getMTGApp().getUiGraph().inject(this);
        cardsPresenter.init(this);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCard(null);
            }
        });

        if (bundle == null) {
            luckyCards = new ArrayList<>();
        } else {
            luckyCards = bundle.getParcelableArrayList("luckyCards");
        }

        cardsPresenter.loadIdFavourites();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("luckyCards", luckyCards);
    }

    public void onDestroy() {
        super.onDestroy();
        cardsPresenter.detachView();
    }

    public String getPageTrack() {
        return "/lucky-card";
    }

    public void cardLoaded(CardsBucket bucket) {
        LOG.d();
        boolean firstRun = luckyCards.size() <= 1;
        for (MTGCard card : bucket.getCards()) {
            luckyCards.add(card);
            if (card.getImage() != null) {
                // pre-fetch images
                Picasso.with(getApplicationContext()).load(card.getImage()).fetch();
            }
        }
        if (firstRun) {
            refreshCard(null);
        }
    }

    @Override
    public void deckLoaded(DeckBucket bucket) {
        throw new UnsupportedOperationException();
    }

    public void favIdLoaded(int[] favourites) {
        LOG.d();
        idFavourites = favourites.clone();
        if (luckyCards.size() == 0) {
            if (getIntent() != null && getIntent().hasExtra(CARD)) {
                luckyCards.add((MTGCard) getIntent().getParcelableExtra(CARD));
            }
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS);
        } else {
            updateMenu();
        }
    }

    @Override
    public void cardTypePreferenceChanged(boolean grid) {
        throw new UnsupportedOperationException();
    }

    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(MTGException exception) {

    }

    @OnClick(R.id.lucky_again)
    public void refreshCard(View view) {
        LOG.d();
        if (luckyCards == null || luckyCards.isEmpty()) {
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS);
            return;
        }
        MTGCard card = luckyCards.remove(0);
        cardView.load(card, cardsPreferences.showImage());
        if (luckyCards.size() <= 2) {
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS);
        }
        updateMenu();
    }

    public MTGCard getCurrentCard() {
        LOG.d();
        return cardView.getCard();
    }

    public void toggleImage(boolean show) {
        LOG.d();
        cardView.toggleImage(show);
    }

    public void favClicked() {
        LOG.d();
        MTGCard currentCard = cardView.getCard();
        boolean favInCollection = ArrayUtils.contains(idFavourites, currentCard.getMultiVerseId());
        if (favInCollection) {
            cardsPresenter.removeFromFavourite(currentCard, true);
        } else {
            cardsPresenter.saveAsFavourite(currentCard, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lucky_card, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_to_deck) {
            openDialog("add_to_deck", AddToDeckFragment.newInstance(cardView.getCard()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}