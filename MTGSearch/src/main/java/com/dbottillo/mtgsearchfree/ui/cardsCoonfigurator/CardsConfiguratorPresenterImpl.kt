package com.dbottillo.mtgsearchfree.ui.cardsCoonfigurator

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory

class CardsConfiguratorPresenterImpl(val cardFilterInteractor: CardFilterInteractor,
                                     val runnerFactory: RunnerFactory) : CardsConfiguratorPresenter {

    val filterWrapper: Runner<CardFilter> = runnerFactory.simple()

    lateinit var view: CardsConfiguratorView

    var filter: CardFilter? = null

    override fun init(view: CardsConfiguratorView) {
        this.view = view

        filterWrapper.run(cardFilterInteractor.load(), object : Runner.RxWrapperListener<CardFilter> {
            override fun onNext(data: CardFilter) {
                filter = data
            }

            override fun onError(e: Throwable?) {

            }

            override fun onCompleted() {
                filter?.let { view.loadFilter(it) }
            }
        })
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        when (type) {
            CardFilter.TYPE.WHITE -> filter?.white = on
            CardFilter.TYPE.BLUE -> filter?.blue = on
            CardFilter.TYPE.RED -> filter?.red = on
            CardFilter.TYPE.BLACK -> filter?.black = on
            CardFilter.TYPE.GREEN -> filter?.green = on
            CardFilter.TYPE.LAND -> filter?.land = on
            CardFilter.TYPE.ELDRAZI -> filter?.eldrazi = on
            CardFilter.TYPE.ARTIFACT -> filter?.artifact = on
            CardFilter.TYPE.COMMON -> filter?.common = on
            CardFilter.TYPE.UNCOMMON -> filter?.uncommon = on
            CardFilter.TYPE.RARE -> filter?.rare = on
            CardFilter.TYPE.MYTHIC -> filter?.mythic = on
            CardFilter.TYPE.SORT_WUBGR -> filter?.sortWUBGR = on
        }
        cardFilterInteractor.sync(filter)
        filter?.let { view.loadFilter(it) }
    }

}
