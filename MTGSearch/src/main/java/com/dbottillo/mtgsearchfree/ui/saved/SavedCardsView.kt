package com.dbottillo.mtgsearchfree.ui.saved

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.view.BasicView

interface SavedCardsView : BasicView {

    fun showCardsGrid()

    fun showCardsList()

    fun showCards(cardsCollection: CardsCollection)

}