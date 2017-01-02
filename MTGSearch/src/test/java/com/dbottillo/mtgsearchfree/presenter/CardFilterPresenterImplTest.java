package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import rx.Observable;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardFilterPresenterImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CardFilterPresenter underTest;

    @Mock
    CardFilterInteractor interactor;

    @Mock
    CardFilterView view;

    @Mock
    CardFilter cardFilter;

    @Mock
    Logger logger;

    @Before
    public void setup() {
        when(interactor.load()).thenReturn(Observable.just(cardFilter));
        underTest = new CardFilterPresenterImpl(interactor, new TestRunnerFactory(), new MemoryStorage(logger), logger);
        underTest.init(view);
    }

    @Test
    public void testLoadFilter() {
        underTest.loadFilter();
        verify(view).filterLoaded(cardFilter);
        verify(interactor).load();
    }

    @Test
    public void testLoadFilterWillUseCacheAfterFirstCall() {
        underTest.loadFilter();
        underTest.loadFilter();
        verify(view, times(2)).filterLoaded(cardFilter);
        verify(interactor, times(1)).load();
    }

    @Test
    public void testUpdate() {
        underTest.loadFilter(); // need to load it first
        underTest.update(CardFilter.TYPE.BLUE, true);
        assertTrue(cardFilter.blue);
        verify(interactor).sync(cardFilter);
    }
}