package com.dbottillo.mtgsearchfree.ui.search

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable

class SearchPresenterImpl(
    private val setsInteractor: SetsInteractor,
    private val cardsInteractor: CardsInteractor,
    private val generalData: GeneralData,
    private val logger: Logger
) : SearchPresenter {

    lateinit var view: SearchActivityView

    private var disposable = CompositeDisposable()

    override fun init(view: SearchActivityView) {
        this.view = view
        logger.d("created")
        if (generalData.isCardsShowTypeGrid) {
            view.showCardsGrid()
        } else {
            view.showCardsList()
        }
    }

    override fun loadSet() {
        disposable.add(setsInteractor.load().subscribe({
            view.setLoaded(it)
        }, {
            logger.logNonFatal(it)
        }))
    }

    override fun doSearch(searchParams: SearchParams) {
        disposable.add(cardsInteractor.doSearch(searchParams).subscribe({
            view.showSearch(it)
        }, {
            logger.logNonFatal(it)
        }))
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

    override fun onDestroy() {
        disposable.clear()
    }
}