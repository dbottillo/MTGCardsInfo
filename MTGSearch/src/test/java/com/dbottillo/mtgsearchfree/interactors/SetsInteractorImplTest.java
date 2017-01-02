package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.util.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SetsInteractorImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Logger logger;

    private List<MTGSet> sets = Arrays.asList(new MTGSet(1, "Zendikar"), new MTGSet(2, "Ravnica"));

    @Test
    public void testLoad() throws Exception {
        SetDataSource setDataSource = mock(SetDataSource.class);
        when(setDataSource.getSets()).thenReturn(sets);
        SetsInteractor interactor = new SetsInteractorImpl(setDataSource, logger);
        TestSubscriber<List<MTGSet>> testSubscriber = new TestSubscriber<>();
        interactor.load().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(sets));
        verify(setDataSource).getSets();
    }
}