package com.dbottillo.mtgsearchfree.ui.search

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger

class SearchPresenterImpl(val setsInteractor: SetsInteractor,
                          val cardsInteractor: CardsInteractor,
                          val generalData: GeneralData,
                          val factory: RunnerFactory,
                          val logger: Logger) : SearchPresenter {

    val runnerSet: Runner<List<MTGSet>> = factory.simple()
    val runnerSearch: Runner<CardsCollection> = factory.simple()

    lateinit var view: SearchActivityView

    override fun init(view: SearchActivityView) {
        this.view = view
        if (generalData.isCardsShowTypeGrid) {
            view.showCardsList()
        } else {
            view.showCardsGrid()
        }
    }

    override fun loadSet() {
        runnerSet.run(setsInteractor.load(), object : Runner.RxWrapperListener<List<MTGSet>>{
            override fun onNext(data: List<MTGSet>) {
                view.setLoaded(data)
            }

            override fun onError(e: Throwable?) {
            }

            override fun onCompleted() {
            }

        })
    }

    override fun doSearch(searchParams: SearchParams) {
        runnerSearch.run(cardsInteractor.doSearch(searchParams), object : Runner.RxWrapperListener<CardsCollection>{
            override fun onError(e: Throwable?) {
            }

            override fun onCompleted() {
            }

            override fun onNext(data: CardsCollection) {
                view.showSearch(data)
            }

        })
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