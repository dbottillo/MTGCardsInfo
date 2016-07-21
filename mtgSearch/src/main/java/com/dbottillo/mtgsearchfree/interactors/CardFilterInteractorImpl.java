package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.LOG;

import rx.Observable;

public class CardFilterInteractorImpl implements CardFilterInteractor {

    CardsPreferences cardsPreferences;

    public CardFilterInteractorImpl(CardsPreferences cardsPreferences) {
        LOG.d("created");
        this.cardsPreferences = cardsPreferences;
    }

    public Observable<CardFilter> load() {
        LOG.d("loadSet");
        return Observable.just(cardsPreferences.load());
    }

    public void sync(CardFilter filter) {
        LOG.d("sync");
        cardsPreferences.sync(filter);
    }

}
