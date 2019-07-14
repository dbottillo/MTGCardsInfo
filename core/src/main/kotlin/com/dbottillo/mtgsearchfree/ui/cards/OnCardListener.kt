package com.dbottillo.mtgsearchfree.ui.cards

import android.view.MenuItem

import com.dbottillo.mtgsearchfree.model.MTGCard

interface OnCardListener {
    fun onTitleHeaderSelected()
    fun onCardsViewTypeSelected()
    fun onCardsSettingSelected()
    fun onCardSelected(card: MTGCard, position: Int)
    fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int)
}