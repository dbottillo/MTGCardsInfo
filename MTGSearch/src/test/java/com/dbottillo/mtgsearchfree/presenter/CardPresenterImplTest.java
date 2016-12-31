package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.CardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardPresenterImplTest {

    private CardPresenter underTest;

    @Mock
    CardsInteractor cardsInteractor;

    @Mock
    Logger logger;

    @Mock
    CardView cardView;

    @Mock
    MTGCard card;

    @Mock
    MTGCard otherCard;

    @Before
    public void setUp() throws Exception {
        when(cardsInteractor.loadOtherSideCard(card)).thenReturn(Observable.just(otherCard));
        underTest = new CardPresenterImpl(cardsInteractor, logger, new TestRunnerFactory());
        underTest.init(cardView);
    }

    @Test
    public void loadCardWithMultiverseId() {
        underTest.loadOtherSideCard(card);

        verify(cardsInteractor).loadOtherSideCard(card);
        ArgumentCaptor<MTGCard> argument = ArgumentCaptor.forClass(MTGCard.class);
        verify(cardView).otherSideCardLoaded(argument.capture());
        assertThat(argument.getValue(), is(otherCard));
    }

}