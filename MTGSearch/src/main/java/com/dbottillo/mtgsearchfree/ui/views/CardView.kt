package com.dbottillo.mtgsearchfree.ui.views

import com.dbottillo.mtgsearchfree.model.MTGCard

interface CardView {
    fun otherSideCardLoaded(card: MTGCard)
}