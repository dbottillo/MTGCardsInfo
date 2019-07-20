package com.dbottillo.mtgsearchfree.saved

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.ui.BasicView
import com.dbottillo.mtgsearchfree.ui.LoadingView

interface SavedCardsView : BasicView, LoadingView {
    fun showCardsGrid()
    fun showCardsList()
    fun showCards(cardsCollection: CardsCollection)
    fun showEmptyScreen()
}