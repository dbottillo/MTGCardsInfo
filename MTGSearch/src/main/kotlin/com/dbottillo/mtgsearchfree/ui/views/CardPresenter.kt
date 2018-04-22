package com.dbottillo.mtgsearchfree.ui.views

import com.dbottillo.mtgsearchfree.model.MTGCard

interface CardPresenter {
    fun loadOtherSideCard(card: MTGCard)
    fun init(cardView: CardView)
}
