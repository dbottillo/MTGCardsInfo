package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.view.SetsView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SetsPresenterImplTest extends BaseTest {

    private SetsView view;
    private SetsInteractor interactor;
    private SetsPresenter presenter;

    @Mock
    List<MTGSet> sets;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MemoryStorage memoryStorage = new MemoryStorage();
        interactor = mock(SetsInteractor.class);
        view = mock(SetsView.class);
        when(interactor.load()).thenReturn(Observable.just(sets));
        presenter = new SetsPresenterImpl(interactor, new TestRunnerFactory(),
                mock(CardsPreferences.class), memoryStorage);
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