package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

import rx.Observable;

public class SetsInteractorImpl implements SetsInteractor {

    SetDataSource storage;

    public SetsInteractorImpl(SetDataSource storage) {
        LOG.d("created");
        this.storage = storage;
    }

    public Observable<List<MTGSet>> load() {
        LOG.d("loadSet sets");
        return Observable.just(storage.getSets());
    }

}
