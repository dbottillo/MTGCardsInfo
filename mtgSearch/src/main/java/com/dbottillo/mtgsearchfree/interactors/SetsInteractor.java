package com.dbottillo.mtgsearchfree.interactors;


import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.List;

import rx.Observable;

public interface SetsInteractor {

    Observable<List<MTGSet>> load();
}