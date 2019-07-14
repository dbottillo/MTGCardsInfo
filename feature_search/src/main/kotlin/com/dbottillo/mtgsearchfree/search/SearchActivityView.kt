package com.dbottillo.mtgsearchfree.search

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGSet

interface SearchActivityView {
    fun setLoaded(data: List<MTGSet>)
    fun showSearch(data: CardsCollection)
    fun showCardsList()
    fun showCardsGrid()
}