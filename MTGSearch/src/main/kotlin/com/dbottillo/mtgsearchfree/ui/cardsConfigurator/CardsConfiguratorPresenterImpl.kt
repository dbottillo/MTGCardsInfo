package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.CardFilter

class CardsConfiguratorPresenterImpl(private val cardFilterInteractor: CardFilterInteractor) : CardsConfiguratorPresenter {

    lateinit var view: CardsConfiguratorView

    var filter: CardFilter? = null

    override fun init(view: CardsConfiguratorView) {
        this.view = view

        cardFilterInteractor.load().subscribe({
            filter = it
            view.loadFilter(filter = it, refresh = false)
        })
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        filter?.let { filter ->
            when (type) {
                CardFilter.TYPE.WHITE -> filter.white = on
                CardFilter.TYPE.BLUE -> filter.blue = on
                CardFilter.TYPE.RED -> filter.red = on
                CardFilter.TYPE.BLACK -> filter.black = on
                CardFilter.TYPE.GREEN -> filter.green = on
                CardFilter.TYPE.LAND -> filter.land = on
                CardFilter.TYPE.ELDRAZI -> filter.eldrazi = on
                CardFilter.TYPE.ARTIFACT -> filter.artifact = on
                CardFilter.TYPE.COMMON -> filter.common = on
                CardFilter.TYPE.UNCOMMON -> filter.uncommon = on
                CardFilter.TYPE.RARE -> filter.rare = on
                CardFilter.TYPE.MYTHIC -> filter.mythic = on
                CardFilter.TYPE.SORT_WUBGR -> filter.sortWUBGR = on
            }
            cardFilterInteractor.sync(filter)
            view.loadFilter(filter = filter, refresh = true)
        }
    }

}
