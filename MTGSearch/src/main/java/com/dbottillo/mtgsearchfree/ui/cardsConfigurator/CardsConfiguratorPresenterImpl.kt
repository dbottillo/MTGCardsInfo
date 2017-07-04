package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.CardFilter

class CardsConfiguratorPresenterImpl(val cardFilterInteractor: CardFilterInteractor) : CardsConfiguratorPresenter {

    lateinit var view: CardsConfiguratorView

    var filter: CardFilter? = null

    override fun init(view: CardsConfiguratorView) {
        this.view = view

        cardFilterInteractor.load().subscribe({
            filter = it
            view.loadFilter(it)
        })
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        filter?.let { 
            when (type) {
                CardFilter.TYPE.WHITE -> it.white = on
                CardFilter.TYPE.BLUE -> it.blue = on
                CardFilter.TYPE.RED -> it.red = on
                CardFilter.TYPE.BLACK -> it.black = on
                CardFilter.TYPE.GREEN -> it.green = on
                CardFilter.TYPE.LAND -> it.land = on
                CardFilter.TYPE.ELDRAZI -> it.eldrazi = on
                CardFilter.TYPE.ARTIFACT -> it.artifact = on
                CardFilter.TYPE.COMMON -> it.common = on
                CardFilter.TYPE.UNCOMMON -> it.uncommon = on
                CardFilter.TYPE.RARE -> it.rare = on
                CardFilter.TYPE.MYTHIC -> it.mythic = on
                CardFilter.TYPE.SORT_WUBGR -> it.sortWUBGR = on
            }
            cardFilterInteractor.sync(it)
            view.loadFilter(it)
        }
    }

}
