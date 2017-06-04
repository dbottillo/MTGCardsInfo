package com.dbottillo.mtgsearchfree.ui.cards

import android.content.Intent
import com.dbottillo.mtgsearchfree.model.MTGCard

interface CardsActivityPresenter{
    fun init(view: CardsActivityView, intent: Intent?)
    fun isDeck(): Boolean
    fun updateMenu(currentCard: MTGCard?)
    fun favClicked(currentCard: MTGCard?)
    fun toggleImage(show: Boolean)
}
