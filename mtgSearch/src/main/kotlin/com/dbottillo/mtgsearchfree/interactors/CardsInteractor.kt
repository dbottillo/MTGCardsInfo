package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import rx.Observable

interface CardsInteractor {

    fun load(set: MTGSet): Observable<List<MTGCard>>

    fun saveAsFavourite(card: MTGCard)
}