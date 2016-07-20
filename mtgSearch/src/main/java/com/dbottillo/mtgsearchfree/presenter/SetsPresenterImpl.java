package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.SetsView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class SetsPresenterImpl implements SetsPresenter, RxWrapper.RxWrapperListener<List<MTGSet>> {

    private SetsInteractor interactor;
    private SetsView setView;
    private CardsPreferences cardsPreferences;
    private RxWrapper<List<MTGSet>> wrapper;
    private MemoryStorage memoryStorage;
    private int currentSetPosition = -1;

    @Inject
    public SetsPresenterImpl(SetsInteractor interactor, RxWrapper<List<MTGSet>> wrapper,
                             CardsPreferences cardsPreferences, MemoryStorage memoryStorage) {
        LOG.d("created");
        this.interactor = interactor;
        this.wrapper = wrapper;
        this.cardsPreferences = cardsPreferences;
        this.memoryStorage = memoryStorage;
    }

    public void init(SetsView view) {
        LOG.d();
        setView = view;
    }

    public void loadSets() {
        LOG.d();
        if (memoryStorage.isInit()) {
            setView.setsLoaded(memoryStorage.getSets());
            return;
        }
        Observable<List<MTGSet>> obs = interactor.load();
        wrapper.run(obs, this);
    }

    @Override
    public void setSelected(int position) {
        currentSetPosition = position;
        cardsPreferences.saveSetPosition(position);
    }

    @Override
    public int getCurrentSetPosition() {
        if (currentSetPosition < 0){
            currentSetPosition = cardsPreferences.getSetPosition();
        }
        return currentSetPosition;
    }

    @Override
    public void onNext(List<MTGSet> mtgSets) {
        LOG.d();
        memoryStorage.setInit(true);
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