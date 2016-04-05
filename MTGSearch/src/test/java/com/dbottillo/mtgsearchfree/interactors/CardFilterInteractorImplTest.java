package com.dbottillo.mtgsearchfree.interactors;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class CardFilterInteractorImplTest {

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
    }

    @Test
    public void willSyncDataWithStorage() {
        CardFilterStorage storage = mock(CardFilterStorage.class);
        CardFilterInteractorImpl interactor = new CardFilterInteractorImpl(storage);
        interactor.sync(cardFilter);
        verify(storage).sync(cardFilter);
    }

}