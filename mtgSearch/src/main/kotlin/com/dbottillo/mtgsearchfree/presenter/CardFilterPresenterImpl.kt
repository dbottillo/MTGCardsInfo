package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CardFilterPresenterImpl(var interactor: CardFilterInteractor) : CardFilterPresenter {

    var filterView: CardFilterView? = null

    override fun init(view: CardFilterView) {
        filterView = view
    }

    override fun loadFilter() {
        if (CardFilterMemoryStorage.init) {
            filterLoaded()
        } else {
            var obs = interactor.load()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
            obs.subscribe {
                CardFilterMemoryStorage.init = true
                CardFilterMemoryStorage.filter = it
                filterLoaded()
            }
        }
    }

    private fun filterLoaded() {
        filterView?.filterLoaded(CardFilterMemoryStorage.filter)
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        when (type) {
            CardFilter.TYPE.WHITE -> CardFilterMemoryStorage.filter.white = on
            CardFilter.TYPE.BLUE -> CardFilterMemoryStorage.filter.blue = on
            CardFilter.TYPE.BLACK -> CardFilterMemoryStorage.filter.black = on
            CardFilter.TYPE.RED -> CardFilterMemoryStorage.filter.red = on
            CardFilter.TYPE.GREEN -> CardFilterMemoryStorage.filter.green = on
            CardFilter.TYPE.LAND -> CardFilterMemoryStorage.filter.land = on
            CardFilter.TYPE.ELDRAZI -> CardFilterMemoryStorage.filter.eldrazi = on
            CardFilter.TYPE.ARTIFACT -> CardFilterMemoryStorage.filter.artifact = on
            CardFilter.TYPE.COMMON -> CardFilterMemoryStorage.filter.common = on
            CardFilter.TYPE.UNCOMMON -> CardFilterMemoryStorage.filter.uncommon = on
            CardFilter.TYPE.RARE -> CardFilterMemoryStorage.filter.rare = on
            CardFilter.TYPE.MYTHIC -> CardFilterMemoryStorage.filter.eldrazi = on
        }
        interactor.sync(CardFilterMemoryStorage.filter)
        filterLoaded()
    }
}
