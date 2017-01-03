package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.CardFilter;

import io.reactivex.Observable;

public interface CardFilterInteractor {

    Observable<CardFilter> load();

    void sync(CardFilter filter);

}