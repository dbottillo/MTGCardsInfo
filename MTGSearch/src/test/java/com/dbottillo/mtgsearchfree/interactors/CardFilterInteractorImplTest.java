package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardFilterInteractorImplTest extends BaseTest{

    @Mock
    CardFilter cardFilter;

    @Test
    public void willLoadDataFromStorage() {
        CardsPreferencesImpl cardsPreferences = mock(CardsPreferencesImpl.class);
        when(cardsPreferences.load()).thenReturn(cardFilter);
        CardFilterInteractorImpl interactor = new CardFilterInteractorImpl(cardsPreferences);
        TestSubscriber<CardFilter> testSubscriber = new TestSubscriber<>();
        interactor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Arrays.asList(cardFilter));
        verify(cardsPreferences).load();
    }

    @Test
    public void willSyncDataWithStorage() {
        CardsPreferencesImpl cardsPreferences = mock(CardsPreferencesImpl.class);
        CardFilterInteractorImpl interactor = new CardFilterInteractorImpl(cardsPreferences);
        interactor.sync(cardFilter);
        verify(cardsPreferences).sync(cardFilter);
    }

}