package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.SetsView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SetsPresenterImpl implements SetsPresenter {

    SetsInteractor interactor;
    SetsView setView;

    public SetsPresenterImpl(SetsInteractor interactor) {
        LOG.d("created");
        this.interactor = interactor;
    }

    public void init(SetsView view) {
        LOG.d();
        setView = view;
    }

    public void loadSets() {
        LOG.d();
        if (SetsMemoryStorage.init) {
            LOG.d("sets already in memory, just return");
            setView.setsLoaded(SetsMemoryStorage.sets);
            return;
        }
        Observable<List<MTGSet>> obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        LOG.d("obs created");
        obs.subscribe(new Observer<List<MTGSet>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<MTGSet> mtgSets) {
                LOG.d();
                SetsMemoryStorage.init = true;
                SetsMemoryStorage.sets = mtgSets;
                setView.setsLoaded(mtgSets);
            }
        });

    }

}