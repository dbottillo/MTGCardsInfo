package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.resources.CardsBucket

interface CardsView : BasicView {

    fun cardLoaded(bucket: CardsBucket)

    fun favIdLoaded(favourites: IntArray)
}