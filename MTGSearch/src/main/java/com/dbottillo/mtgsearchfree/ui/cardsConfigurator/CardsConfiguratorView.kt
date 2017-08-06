package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import com.dbottillo.mtgsearchfree.model.CardFilter

interface CardsConfiguratorView {
    fun loadFilter(filter: CardFilter)
}
