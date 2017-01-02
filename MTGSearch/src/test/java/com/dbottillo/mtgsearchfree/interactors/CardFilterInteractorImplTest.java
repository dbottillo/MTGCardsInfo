package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardFilterInteractorImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    CardFilter cardFilter;

    @Mock
    CardsPreferencesImpl cardsPreferences;

    @Mock
    Logger logger;

    private CardFilterInteractor underTest;

    @Before
    public void setUp() throws Exception {
        when(cardsPreferences.load()).thenReturn(cardFilter);
        underTest = new CardFilterInteractorImpl(cardsPreferences, logger);
    }

    @Test
    public void willLoadDataFromStorage() {
        TestSubscriber<CardFilter> testSubscriber = new TestSubscriber<>();
        underTest.load().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(cardFilter));
        verify(cardsPreferences).load();
    }

    @Test
    public void willSyncDataWithStorage() {
        underTest.sync(cardFilter);

        verify(cardsPreferences).sync(cardFilter);
    }

}