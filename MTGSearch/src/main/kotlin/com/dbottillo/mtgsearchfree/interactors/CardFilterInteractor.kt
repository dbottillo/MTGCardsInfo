package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.CardFilter

import io.reactivex.Observable

interface CardFilterInteractor {
    fun load(): Observable<CardFilter>
    fun sync(filter: CardFilter)
}