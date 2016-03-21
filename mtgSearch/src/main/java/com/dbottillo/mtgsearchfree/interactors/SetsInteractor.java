package com.dbottillo.mtgsearchfree.interactors;


import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.ArrayList;

import rx.Observable;

public interface SetsInteractor {

    Observable<ArrayList<MTGSet>> load();
}