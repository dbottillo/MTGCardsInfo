package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import io.reactivex.Observable;

public class SetsInteractorImpl implements SetsInteractor {

    private final SetDataSource storage;
    private final Logger logger;

    public SetsInteractorImpl(SetDataSource storage, Logger logger) {
        this.logger = logger;
        this.storage = storage;
        logger.d("created");
    }

    public Observable<List<MTGSet>> load() {
        logger.d("loadSet sets");
        return Observable.just(storage.getSets());
    }

}
