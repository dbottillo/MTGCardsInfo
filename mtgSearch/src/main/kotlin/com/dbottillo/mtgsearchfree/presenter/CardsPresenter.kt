package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.view.CardsView

interface CardsPresenter {

    fun init(view: CardsView)

    fun loadCards(set: MTGSet)

    fun saveAsFavourite(card: MTGCard)
}