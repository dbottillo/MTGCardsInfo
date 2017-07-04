package com.dbottillo.mtgsearchfree.ui.search

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger

class SearchPresenterImpl(val setsInteractor: SetsInteractor,
                          val cardsInteractor: CardsInteractor,
                          val generalData: GeneralData,
                          val logger: Logger) : SearchPresenter {

    lateinit var view: SearchActivityView

    override fun init(view: SearchActivityView) {
        this.view = view
        if (generalData.isCardsShowTypeGrid) {
            view.showCardsGrid()
        } else {
            view.showCardsList()
        }
    }

    override fun loadSet() {
        setsInteractor.load().subscribe {
            view.setLoaded(it)
        }
    }

    override fun doSearch(searchParams: SearchParams) {
        cardsInteractor.doSearch(searchParams).subscribe {
            view.showSearch(it)
        }
    }

    override fun toggleCardTypeViewPreference() {
        if (generalData.isCardsShowTypeGrid) {
            generalData.setCardsShowTypeList()
            view.showCardsList()
        } else {
            generalData.setCardsShowTypeGrid()
            view.showCardsGrid()
        }
    }

    override fun saveAsFavourite(card: MTGCard) {
        cardsInteractor.saveAsFavourite(card)
    }
}