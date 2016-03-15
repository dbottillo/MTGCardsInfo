package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.view.CardsView

interface CardsPresenter : BasicPresenter {

    fun init(view: CardsView)

    fun loadCards(set: MTGSet)

    fun loadIdFavourites()

    fun saveAsFavourite(card: MTGCard)

    fun removeFromFavourite(card: MTGCard)

    fun getLuckyCards(howMany: Int)
}