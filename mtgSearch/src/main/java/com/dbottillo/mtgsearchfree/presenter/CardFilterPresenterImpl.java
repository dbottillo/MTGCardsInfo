package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CardFilterPresenterImpl implements CardFilterPresenter {

    CardFilterInteractor interactor;
    CardFilterView filterView;

    public CardFilterPresenterImpl(CardFilterInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void init(CardFilterView view) {
        filterView = view;
    }

    public void loadFilter() {
        if (CardFilterMemoryStorage.init) {
            filterLoaded();
        } else {
            Observable<CardFilter> obs = interactor.load()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
            obs.subscribe(new Observer<CardFilter>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(CardFilter cardFilter) {
                    CardFilterMemoryStorage.init = true;
                    CardFilterMemoryStorage.filter = cardFilter;
                    filterLoaded();
                }
            });
        }
    }

    private void filterLoaded() {
        filterView.filterLoaded(CardFilterMemoryStorage.filter);
    }

    public void update(CardFilter.TYPE type, boolean on) {
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
