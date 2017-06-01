package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGSet

import io.reactivex.Observable

interface SetsInteractor {

    fun load(): Observable<List<MTGSet>>
}