package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

import rx.Observable;

public class SetsInteractorImpl implements SetsInteractor {

    SetsStorage storage;

    public SetsInteractorImpl(SetsStorage storage) {
        this.storage = storage;
    }

    public Observable<ArrayList<MTGSet>> load() {
        return Observable.just(storage.load());
    }

}
