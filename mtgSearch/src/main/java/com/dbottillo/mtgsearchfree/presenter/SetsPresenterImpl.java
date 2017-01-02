package com.dbottillo.mtgsearchfree.presenter;

import android.util.Log;

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.SetsView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class SetsPresenterImpl implements SetsPresenter, Runner.RxWrapperListener<List<MTGSet>> {

    private final SetsInteractor interactor;
    private SetsView setView;
    private final CardsPreferences cardsPreferences;
    private final Runner<List<MTGSet>> wrapper;
    private final MemoryStorage memoryStorage;
    private final Logger logger;

    @Inject
    public SetsPresenterImpl(SetsInteractor interactor, RunnerFactory runnerFactory,
                             CardsPreferences cardsPreferences, MemoryStorage memoryStorage, Logger logger) {
        this.logger = logger;
        this.interactor = interactor;
        this.wrapper = runnerFactory.simple();
        this.cardsPreferences = cardsPreferences;
        this.memoryStorage = memoryStorage;
        logger.d("created");
    }

    public void init(SetsView view) {
        logger.d();
        setView = view;
    }

    public void loadSets() {
        logger.d();
        if (memoryStorage.getSets() != null) {
            setView.setsLoaded(memoryStorage.getSets());
            return;
        }
        Observable<List<MTGSet>> obs = interactor.load();
        wrapper.run(obs, this);
    }

    @Override
    public void setSelected(int position) {
        cardsPreferences.saveSetPosition(position);
    }

    @Override
    public int getCurrentSetPosition() {
        return cardsPreferences.getSetPosition();
    }

    @Override
    public void onNext(List<MTGSet> mtgSets) {
        logger.d();
        memoryStorage.setSets(mtgSets);
        setView.setsLoaded(mtgSets);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {

    }

}