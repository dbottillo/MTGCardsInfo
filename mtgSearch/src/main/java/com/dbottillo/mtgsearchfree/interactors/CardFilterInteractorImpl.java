package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;
import com.dbottillo.mtgsearchfree.util.LOG;

import rx.Observable;

public class CardFilterInteractorImpl implements CardFilterInteractor {

    CardFilterStorage cardFilterStorage;

    public CardFilterInteractorImpl(CardFilterStorage cardFilterStorage) {
        LOG.d("created");
        this.cardFilterStorage = cardFilterStorage;
    }

    public Observable<CardFilter> load() {
        LOG.d("load");
        return Observable.just(cardFilterStorage.load());
    }

    public void sync(CardFilter filter) {
        LOG.d("sync");
        cardFilterStorage.sync(filter);
    }

}
