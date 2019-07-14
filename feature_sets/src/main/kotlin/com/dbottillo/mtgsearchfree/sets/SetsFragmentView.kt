package com.dbottillo.mtgsearchfree.sets

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGSet

interface SetsFragmentView {
    fun showSet(set: MTGSet, cardsCollection: CardsCollection)
    fun showCardsList()
    fun showCardsGrid()
    fun showLoading()
    fun hideLoading()
}