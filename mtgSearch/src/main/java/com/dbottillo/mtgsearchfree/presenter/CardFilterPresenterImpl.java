package com.dbottillo.mtgsearchfree.presenter;

import android.util.Log;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CardFilterPresenterImpl implements CardFilterPresenter, RxWrapper.RxWrapperListener<CardFilter> {

    CardFilterInteractor interactor;
    CardFilterView filterView;

    @Inject
    RxWrapper<CardFilter> wrapper;

    public CardFilterPresenterImpl(CardFilterInteractor interactor) {
        LOG.d("created");
        MTGApp.graph.inject(this);
        this.interactor = interactor;
    }

    @Override
    public void init(CardFilterView view) {
        LOG.d();
        filterView = view;
    }

    public void loadFilter() {
        LOG.d();
        if (CardFilterMemoryStorage.init) {
            LOG.d("filters already in memory, will just return");
            filterLoaded();
        } else {
            LOG.d("obs created and now subscribe");
            wrapper.run(interactor.load(), this);
        }
    }

    @Override
    public void onNext(CardFilter cardFilter) {
        LOG.d();
        CardFilterMemoryStorage.init = true;
        CardFilterMemoryStorage.filter = cardFilter;
        filterLoaded();
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {

    }

    private void filterLoaded() {
        LOG.d();
        filterView.filterLoaded(CardFilterMemoryStorage.filter);
    }

    public void update(CardFilter.TYPE type, boolean on) {
        LOG.d("update " + type.toString() + "with: " + on);
        switch (type) {
            case WHITE: {
                CardFilterMemoryStorage.filter.white = on;
                break;
            }
            case BLUE: {
                CardFilterMemoryStorage.filter.blue = on;
                break;
            }
            case RED: {
                CardFilterMemoryStorage.filter.red = on;
                break;
            }
            case BLACK: {
                CardFilterMemoryStorage.filter.black = on;
                break;
            }
            case GREEN: {
                CardFilterMemoryStorage.filter.green = on;
                break;
            }
            case LAND: {
                CardFilterMemoryStorage.filter.land = on;
                break;
            }
            case ELDRAZI: {
                CardFilterMemoryStorage.filter.eldrazi = on;
                break;
            }
            case ARTIFACT: {
                CardFilterMemoryStorage.filter.artifact = on;
                break;
            }
            case COMMON: {
                CardFilterMemoryStorage.filter.common = on;
                break;
            }
            case UNCOMMON: {
                CardFilterMemoryStorage.filter.uncommon = on;
                break;
            }
            case RARE: {
                CardFilterMemoryStorage.filter.rare = on;
                break;
            }
            case MYTHIC: {
                CardFilterMemoryStorage.filter.mythic = on;
                break;
            }
        }
        interactor.sync(CardFilterMemoryStorage.filter);
        filterLoaded();
    }

}
