package com.dbottillo.mtgsearchfree.view.views

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.view.CardView

interface CardPresenter {
    fun loadOtherSideCard(card: MTGCard)
    fun init(cardView: CardView)
}
