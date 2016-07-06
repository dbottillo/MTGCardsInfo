package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;

import rx.Observable;

public interface CardFilterInteractor {

    Observable<CardFilter> load();

    void sync(CardFilter filter);

}