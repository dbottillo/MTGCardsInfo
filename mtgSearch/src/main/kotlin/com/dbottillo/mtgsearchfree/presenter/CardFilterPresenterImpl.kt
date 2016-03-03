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
        if (CardFilterMemoryStorage.instance.filter != null) {
            filterLoaded()
        } else {
            var obs = interactor.load()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
            obs.subscribe {
                CardFilterMemoryStorage.instance.filter = it
                filterLoaded()
            }
        }
    }

    private fun filterLoaded() {
        filterView?.filterLoaded(CardFilterMemoryStorage.instance.filter!!)
    }

    override fun update(type: CardFilter.TYPE, on: Boolean) {
        when (type) {
            CardFilter.TYPE.WHITE -> CardFilterMemoryStorage.instance.filter?.white = on
            CardFilter.TYPE.BLUE -> CardFilterMemoryStorage.instance.filter?.blue = on
            CardFilter.TYPE.BLACK -> CardFilterMemoryStorage.instance.filter?.black = on
            CardFilter.TYPE.RED -> CardFilterMemoryStorage.instance.filter?.red = on
            CardFilter.TYPE.GREEN -> CardFilterMemoryStorage.instance.filter?.green = on
            CardFilter.TYPE.LAND -> CardFilterMemoryStorage.instance.filter?.land = on
            CardFilter.TYPE.ELDRAZI -> CardFilterMemoryStorage.instance.filter?.eldrazi = on
            CardFilter.TYPE.ARTIFACT -> CardFilterMemoryStorage.instance.filter?.artifact = on
            CardFilter.TYPE.COMMON -> CardFilterMemoryStorage.instance.filter?.common = on
            CardFilter.TYPE.UNCOMMON -> CardFilterMemoryStorage.instance.filter?.uncommon = on
            CardFilter.TYPE.RARE -> CardFilterMemoryStorage.instance.filter?.rare = on
            CardFilter.TYPE.MYTHIC -> CardFilterMemoryStorage.instance.filter?.eldrazi = on
        }
        interactor.sync(CardFilterMemoryStorage.instance.filter!!)
        filterLoaded()
    }
}
