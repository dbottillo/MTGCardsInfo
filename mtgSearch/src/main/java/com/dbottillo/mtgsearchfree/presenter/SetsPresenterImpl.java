package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.view.SetsView;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SetsPresenterImpl implements SetsPresenter {

    SetsInteractor interactor;
    SetsView setView;

    public SetsPresenterImpl(SetsInteractor interactor) {
        this.interactor = interactor;
    }

    public void init(SetsView view) {
        setView = view;
    }

    public void loadSets() {
        if (SetsMemoryStorage.init) {
            setView.setsLoaded(SetsMemoryStorage.sets);
            return;
        }
        Observable<ArrayList<MTGSet>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe(new Observer<ArrayList<MTGSet>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<MTGSet> mtgSets) {
                SetsMemoryStorage.init = true;
                SetsMemoryStorage.sets = mtgSets;
                setView.setsLoaded(mtgSets);
            }
        });

    }

}