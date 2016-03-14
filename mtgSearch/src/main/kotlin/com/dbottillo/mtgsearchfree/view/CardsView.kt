package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.MTGCard
import java.util.*

interface CardsView : BasicView {

    fun cardLoaded(bucket: CardsBucket)

    fun luckyCardsLoaded(cards: ArrayList<MTGCard>)

    fun favIdLoaded(favourites: IntArray)
}