package com.dbottillo.mtgsearchfree.presenter;

import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class CardFilterPresenterImplTest extends BaseTest {

    CardFilterPresenter presenter;

    CardFilterInteractor interactor;

    CardFilterView view;

    @Mock
    CardFilter cardFilter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        interactor = mock(CardFilterInteractor.class);
        view = mock(CardFilterView.class);
        when(interactor.load()).thenReturn(Observable.just(cardFilter));
        presenter = new CardFilterPresenterImpl(interactor, new TestRxWrapper<CardFilter>());
        presenter.init(view);
        CardFilterMemoryStorage.init = false;
        CardFilterMemoryStorage.filter = null;
    }

    @Test
    public void testLoadFilter() {
        System.out.println("TEST - load filter");
        presenter.loadFilter();
        System.out.println("TEST - checking");
        verify(view).filterLoaded(cardFilter);
        verify(interactor).load();
    }

    @Test
    public void testLoadFilterWillUseCacheAfterFirstCall() {
        presenter.loadFilter();
        presenter.loadFilter();
        verify(view, times(2)).filterLoaded(cardFilter);
        verify(interactor, times(1)).load();
    }

    @Test
    public void testUpdate() {
        presenter.loadFilter(); // need to load it first
        presenter.update(CardFilter.TYPE.BLUE, true);
        assertTrue(cardFilter.blue);
        verify(interactor).sync(cardFilter);
    }
}