package com.dbottillo.mtgsearchfree.ui.cards

import android.view.MenuItem
import android.view.View

import com.dbottillo.mtgsearchfree.model.MTGCard

interface OnCardListener {
    fun onCardsHeaderSelected()
    fun onCardsViewTypeSelected()
    fun onCardsSettingSelected()
    fun onCardSelected(card: MTGCard, position: Int, view: View)
    fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int)
}