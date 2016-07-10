package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.CardFilter;

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
        CardFilterStorage storage = mock(CardFilterStorage.class);
        when(storage.load()).thenReturn(cardFilter);
        CardFilterInteractorImpl interactor = new CardFilterInteractorImpl(storage);
        TestSubscriber<CardFilter> testSubscriber = new TestSubscriber<>();
        interactor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Arrays.asList(cardFilter));
        verify(storage).load();
    }

    @Test
    public void willSyncDataWithStorage() {
        CardFilterStorage storage = mock(CardFilterStorage.class);
        CardFilterInteractorImpl interactor = new CardFilterInteractorImpl(storage);
        interactor.sync(cardFilter);
        verify(storage).sync(cardFilter);
    }

}