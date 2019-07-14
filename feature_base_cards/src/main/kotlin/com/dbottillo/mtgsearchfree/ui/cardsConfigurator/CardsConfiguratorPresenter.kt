package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import com.dbottillo.mtgsearchfree.model.CardFilter

interface CardsConfiguratorPresenter {
    fun init(view: CardsConfiguratorView)
    fun update(type: CardFilter.TYPE, on: Boolean)
    fun onDestroy()
}
