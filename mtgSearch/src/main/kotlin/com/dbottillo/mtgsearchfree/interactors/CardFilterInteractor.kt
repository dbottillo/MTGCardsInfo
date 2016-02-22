package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.resources.CardFilter
import rx.Observable


interface CardFilterInteractor {

    fun load() : Observable<CardFilter>

}