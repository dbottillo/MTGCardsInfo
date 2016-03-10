package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.resources.MTGCard

interface CardsView : BasicView {

    fun cardLoaded(cards: List<MTGCard>)
}