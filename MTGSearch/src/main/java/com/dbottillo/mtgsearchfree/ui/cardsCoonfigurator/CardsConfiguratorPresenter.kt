package com.dbottillo.mtgsearchfree.ui.cardsCoonfigurator

import com.dbottillo.mtgsearchfree.model.CardFilter

interface CardsConfiguratorPresenter{

    fun init(view: CardsConfiguratorView)
    fun update(type: CardFilter.TYPE, on: Boolean)
}
