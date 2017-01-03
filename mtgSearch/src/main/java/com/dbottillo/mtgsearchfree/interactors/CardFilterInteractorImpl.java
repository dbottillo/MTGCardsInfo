package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.Logger;

import io.reactivex.Observable;

public class CardFilterInteractorImpl implements CardFilterInteractor {

    private final CardsPreferences cardsPreferences;
    private final Logger logger;

    public CardFilterInteractorImpl(CardsPreferences cardsPreferences, Logger logger) {
        this.logger = logger;
        this.cardsPreferences = cardsPreferences;
        logger.d("created");
    }

    public Observable<CardFilter> load() {
        logger.d("loadSet");
        return Observable.just(cardsPreferences.load());
    }

    public void sync(CardFilter filter) {
        logger.d("sync");
        cardsPreferences.sync(filter);
    }

}
