package com.dbottillo.mtgsearchfree.interactors;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class SetsInteractorImplTest {

    static List<MTGSet> sets = Arrays.asList(new MTGSet(1, "Zendikar"), new MTGSet(2, "Ravnica"));

    @Test
    public void testLoad() throws Exception {
        SetsStorage storage = mock(SetsStorage.class);
        when(storage.load()).thenReturn(sets);
        SetsInteractor interactor = new SetsInteractorImpl(storage);
        TestSubscriber<List<MTGSet>> testSubscriber = new TestSubscriber<>();
        interactor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(sets));
        verify(storage).load();
    }
}