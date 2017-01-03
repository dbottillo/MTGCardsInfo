package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.List;

import io.reactivex.Observable;

public interface SetsInteractor {

    Observable<List<MTGSet>> load();
}