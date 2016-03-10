package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable

interface SetsInteractor {

    fun load(): Observable<List<MTGSet>>
}