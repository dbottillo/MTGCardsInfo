package com.dbottillo.mtgsearchfree.cards

import android.content.Intent
import android.graphics.Bitmap
import com.dbottillo.mtgsearchfree.model.MTGCard

interface CardsActivityPresenter {
    fun init(view: CardsActivityView, intent: Intent?)
    fun isDeck(): Boolean
    fun updateMenu(currentCard: MTGCard?)
    fun favClicked(currentCard: MTGCard?)
    fun toggleImage(show: Boolean)
    fun shareImage(bitmap: Bitmap)
    fun onDestroy()
}
