package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CardFilterPresenterImpl(var interactor: CardFilterInteractor) : CardFilterPresenter {

    var filter = CardFilter()
    var filterView: CardFilterView? = null

    override fun init(view: CardFilterView) {
        filterView = view
    }

    override fun loadFilter() {
        var obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe {
            filter = it
            filterLoaded()
        }
    }

    private fun filterLoaded() {
        filterView?.filterLoaded(filter)
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        when (type) {
            CardFilter.TYPE.WHITE -> filter.white = on
            CardFilter.TYPE.BLUE -> filter.blue = on
            CardFilter.TYPE.BLACK -> filter.black = on
            CardFilter.TYPE.RED -> filter.red = on
            CardFilter.TYPE.GREEN -> filter.green = on
            CardFilter.TYPE.LAND -> filter.land = on
            CardFilter.TYPE.ELDRAZI -> filter.eldrazi = on
            CardFilter.TYPE.ARTIFACT -> filter.artifact = on
            CardFilter.TYPE.COMMON -> filter.common = on
            CardFilter.TYPE.UNCOMMON -> filter.uncommon = on
            CardFilter.TYPE.RARE -> filter.rare = on
            CardFilter.TYPE.MYTHIC -> filter.eldrazi = on
        }
        interactor.sync(filter)
        filterLoaded()
    }
}
