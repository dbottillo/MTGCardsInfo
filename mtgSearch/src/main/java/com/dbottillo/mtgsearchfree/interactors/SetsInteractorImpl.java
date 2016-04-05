package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;

import rx.Observable;

public class SetsInteractorImpl implements SetsInteractor {

    SetsStorage storage;

    public SetsInteractorImpl(SetsStorage storage) {
        LOG.d("created");
        this.storage = storage;
    }

    public Observable<ArrayList<MTGSet>> load() {
        LOG.d("load sets");
        return Observable.just(storage.load());
    }

}
