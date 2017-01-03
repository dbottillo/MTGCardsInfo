package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.SetsView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import io.reactivex.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SetsPresenterImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SetsView view;

    @Mock
    SetsInteractor interactor;

    @Mock
    SetsPresenter presenter;

    @Mock
    List<MTGSet> sets;

    @Mock
    Logger logger;

    @Before
    public void setup() {
        MemoryStorage memoryStorage = new MemoryStorage(logger);
        interactor = mock(SetsInteractor.class);
        view = mock(SetsView.class);
        when(interactor.load()).thenReturn(Observable.just(sets));
        presenter = new SetsPresenterImpl(interactor, new TestRunnerFactory(),
                mock(CardsPreferencesImpl.class), memoryStorage, logger);
        presenter.init(view);
    }

    @Test
    public void willLoadSetsFromInteractorOnColdStart() throws Exception {
        presenter.loadSets();
        verify(interactor).load();
        verify(view).setsLoaded(sets);
    }

    @Test
    public void cachesSetsAfterFirstRun() throws Exception {
        presenter.loadSets();
        presenter.loadSets();
        verify(interactor, times(1)).load();
        verify(view, times(2)).setsLoaded(sets);
    }

}