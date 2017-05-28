package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet

interface SetsFragmentView {

    fun showSet(set: MTGSet, cards: List<MTGCard>, filter: CardFilter)
    fun showCardsList()
    fun showCardsGrid()

}