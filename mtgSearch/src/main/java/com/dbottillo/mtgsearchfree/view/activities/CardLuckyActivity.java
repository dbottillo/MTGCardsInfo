package com.dbottillo.mtgsearchfree.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardsView;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CardLuckyActivity extends CommonCardsActivity implements CardsView {

    public static String CARD = "CARD";
    public static int LUCKY_BATCH_CARDS = 10;

    private ArrayList<MTGCard> luckyCards = null;

    CardsPresenter cardsPresenter;

    @Bind(R.id.card_view)
    MTGCardView cardView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lucky_card);

        ButterKnife.bind(this);

        setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MTGApp.uiGraph.inject(this);
        cardsPresenter.init(this);

        findViewById(R.id.btn_lucky_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCard();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCard();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void deckLoaded(DeckBucket bucket) {
        throw new UnsupportedOperationException();
    }

    public void luckyCardsLoaded(ArrayList<MTGCard> cards) {
        LOG.d();
        boolean firstRun = luckyCards.size() == 0;
        for (MTGCard card : cards) {
            luckyCards.add(card);
            if (card.getImage() != null) {
                // pre-fetch images
                Picasso.with(getApplicationContext()).load(card.getImage()).fetch();
            }
        }
        if (firstRun) {
            refreshCard();
        }
    }

    public void favIdLoaded(int[] favourites) {
        LOG.d();
        idFavourites = favourites;
        if (luckyCards.size() == 0) {
            if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable(CARD) != null) {
                luckyCards.add((MTGCard) getIntent().getExtras().getParcelable(CARD));
                refreshCard();
            } else {
                cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS);
            }
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

    public void refreshCard() {
        LOG.d();
        if (luckyCards == null || luckyCards.isEmpty()) {
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS);
            return;
        }
        MTGCard card = luckyCards.remove(0);
        SharedPreferences sharedPreferences = getSharedPreferences(MTGApp.PREFS_NAME, 0);
        boolean showImage = sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true);
        cardView.load(card, showImage);
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
        if (Arrays.asList(idFavourites).contains(currentCard.getMultiVerseId())) {
            cardsPresenter.removeFromFavourite(currentCard, true);
        } else {
            cardsPresenter.saveAsFavourite(currentCard, true);
        }
    }

}