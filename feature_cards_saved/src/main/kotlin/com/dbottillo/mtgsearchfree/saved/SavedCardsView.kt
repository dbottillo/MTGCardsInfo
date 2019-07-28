package com.dbottillo.mtgsearchfree.saved

import com.dbottillo.mtgsearchfree.ui.BasicView

interface SavedCardsView : BasicView {
    fun showCardsGrid()
    fun showCardsList()
    fun render(uiModel: SavedCardsUiModel)
}