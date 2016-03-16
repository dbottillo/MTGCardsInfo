package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;

import rx.Observable;

public class CardFilterInteractorImpl implements CardFilterInteractor {

    CardFilterStorage cardFilterStorage;

    public CardFilterInteractorImpl(CardFilterStorage cardFilterStorage) {
        this.cardFilterStorage = cardFilterStorage;
    }

    public Observable<CardFilter> load() {
        return Observable.just(cardFilterStorage.load());
    }

    public void sync(CardFilter filter) {
        cardFilterStorage.sync(filter);
    }

}
