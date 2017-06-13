package com.dbottillo.mtgsearchfree.ui.lucky

import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.BaseCardsView

interface CardsLuckyView : BaseCardsView{
    fun showCard(card: MTGCard, showImage: Boolean)
    fun preFetchCardImage(card: MTGCard)
}